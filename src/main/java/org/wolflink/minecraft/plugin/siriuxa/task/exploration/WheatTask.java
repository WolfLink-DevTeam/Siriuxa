package org.wolflink.minecraft.plugin.siriuxa.task.exploration;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.world.BlockAPI;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.database.OfflinePlayerDB;
import org.wolflink.minecraft.plugin.siriuxa.file.database.OfflinePlayerRecord;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerWheatTaskRecord;
import org.wolflink.minecraft.plugin.siriuxa.file.database.TaskRecordDB;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.loot.ChestLoot;
import org.wolflink.minecraft.plugin.siriuxa.task.common.EvacuationZone;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.common.stage.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.EndStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.ReadyStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.WaitStage;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.LinearStageHolder;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.StageHolder;

import java.util.*;

/**
 * 麦穗任务
 * 任务开局会给一些初始麦穗
 * 完成任务的唯一方式是通过撤离点撤离
 * 麦穗归零/玩家人数为0，任务失败
 */
public class WheatTask extends Task {
    private static final PlayerBackpack defaultKit = new PlayerBackpack();

    static {
        defaultKit.setHelmet(new ItemStack(Material.LEATHER_HELMET));
        defaultKit.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        defaultKit.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        defaultKit.setBoots(new ItemStack(Material.LEATHER_BOOTS));
        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(Material.WOODEN_SWORD));
        items.add(new ItemStack(Material.WOODEN_PICKAXE));
        items.add(new ItemStack(Material.BREAD, 8));
        defaultKit.setItems(items);
    }

    /**
     * 基础麦穗流失量(每秒)
     */
    private final double baseWheatLoss;
    /**
     * 麦穗流失加速度
     */
    private final double wheatLostAcceleratedSpeed;
    /**
     * 麦穗流失倍率
     */
    private double wheatLossMultiple = 1.0;
    /**
     * 本次任务的麦穗余量
     */
    @Getter
    private double taskWheat = 0;
    /**
     * 当前可用的撤离点
     */
    @Getter
    private EvacuationZone availableEvacuationZone = null;
    public void addWheatLossMultiple(double value) {
        wheatLossMultiple += value;
    }

    @Getter
    private final ExplorationDifficulty difficulty;
    @Getter
    private final LinearStageHolder stageHolder = (LinearStageHolder) super.getStageHolder();

    public WheatTask(GlobalTeam globalTeam, ExplorationDifficulty difficulty) {
        super(globalTeam, difficulty, defaultKit);
        this.difficulty = difficulty;
        this.wheatLostAcceleratedSpeed = difficulty.getWheatLostAcceleratedSpeed();
        this.baseWheatLoss = difficulty.getBaseWheatLoss();
    }
    public Set<Player> waitForEvacuatePlayers() {
        if (availableEvacuationZone == null) return new HashSet<>();
        else return availableEvacuationZone.getPlayerInZone();
    }
    private void startEvacuateTask(int minutes) {
        subScheduler.runTaskLater(() -> {
            if (getTaskRegion() == null) return;
            Location evacuateLocation = getTaskRegion().getEvacuateLocation((int) getTaskRegion().getRadius());
            if (evacuateLocation == null) {
                triggerFailed();
                return;
            }
            if (availableEvacuationZone != null) {
                availableEvacuationZone.setAvailable(false);
                availableEvacuationZone = null;
                startEvacuateTask(random.nextInt(12, 20));
            } else {
                availableEvacuationZone = new EvacuationZone(this, evacuateLocation.getWorld(), evacuateLocation.getBlockX(), evacuateLocation.getBlockZ(), 30);
                availableEvacuationZone.setAvailable(true);
                startEvacuateTask(random.nextInt(12, 20));
            }
        }, 20L * 60 * minutes);
    }
    /**
     * 填充玩家任务快照
     */
    @Override
    public void fillRecord(OfflinePlayer offlinePlayer, boolean taskResult) {
        PlayerWheatTaskRecord record = playerRecordMap.get(offlinePlayer.getUniqueId());
        if (record == null) {
            Notifier.error("在尝试补充任务记录数据时，未找到玩家" + offlinePlayer.getName() + "的任务记录类。");
            return;
        }
        record.setWheat(taskWheat / getTaskTeam().getInitSize()); // 保存任务麦穗
        record.setSuccess(taskResult); // 设置任务状态
        PlayerBackpack playerBackpack;
        Player player = offlinePlayer.getPlayer();
        if (player == null || !player.isOnline()) {
            OfflinePlayerRecord offlinePlayerRecord = IOC.getBean(OfflinePlayerDB.class).load(offlinePlayer);
            if (offlinePlayerRecord == null) {
                Notifier.error("在尝试补充任务记录数据时，未找到离线玩家" + offlinePlayer.getName() + "的离线缓存数据。");
                return;
            }
            playerBackpack = offlinePlayerRecord.getPlayerBackpack();
            record.setEscape(true); //标记玩家逃跑
        } else playerBackpack = new PlayerBackpack(player);
        record.setPlayerBackpack(playerBackpack); // 保存玩家背包到任务记录中
    }

    /**
     * 完成任务快照，并保存到本地
     * 在任务结束阶段调用
     */
    @Override
    public void finishRecord() {
        TaskRecordDB taskRecordDB = IOC.getBean(TaskRecordDB.class);
        for (PlayerWheatTaskRecord playerWheatTaskRecord : playerRecordMap.values()) {
            playerWheatTaskRecord.setUsingTimeInMills(getTaskStat().getUsingTimeInMills());
            playerWheatTaskRecord.setFinishedTimeInMills(getTaskStat().getEndTime().getTimeInMillis());
            taskRecordDB.saveRecord(playerWheatTaskRecord);
        }
    }
    /**
     * 撤离玩家
     * (适用于只有部分玩家乘坐撤离飞艇的情况)
     */
    public void evacuate(Player player) {
        fillRecord(player, true);
        getTaskTeam().leave(player);
        Notifier.debug("玩家" + player.getName() + "在任务中先一步撤离了。");
        Notifier.broadcastChat(getTaskTeam().getPlayers(), "玩家" + player.getName() + "已乘坐飞艇撤离。");
        player.teleport(IOC.getBean(Config.class).getLobbyLocation());
        Siriuxa.getInstance().getSubScheduler().runTaskLater(() -> {
            player.sendTitle("§a任务完成", "§7等待任务完全结束后方可领取报酬", 10, 80, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
        }, 20 * 3L);
    }

    private final WheatTaskStat wheatTaskStat = new WheatTaskStat(this);
    @Override
    public WheatTaskStat getTaskStat() {
        return wheatTaskStat;
    }

    @Override
    public void initRecord() {
        for (UUID uuid : getTaskTeam().getMemberUuids()) {
            PlayerWheatTaskRecord record = new PlayerWheatTaskRecord(uuid, this);
            playerRecordMap.put(uuid, record);
        }
    }
    @Override
    protected StageHolder initStageHolder() {
        TaskLinearStageHolder linearStageHolder = new TaskLinearStageHolder(this);
        linearStageHolder.bindStages(new Stage[]{new WaitStage(linearStageHolder), new ReadyStage(linearStageHolder), new GameStage(linearStageHolder), new EndStage(linearStageHolder)});
        // 进入等待阶段
        linearStageHolder.next();
        return linearStageHolder;
    }

    @Override
    public void finish() {
        // do nothing
    }

    @Override
    public void failed() {
        // do nothing
    }
    public void addWheat(double wheat) {
        taskWheat += wheat;
    }

    public void takeWheat(double wheat) {
        taskWheat -= wheat;
        if (taskWheat <= 0) {
            taskWheat = 0;
            triggerFailed();
        }
    }
    /**
     * 获取当前麦穗每秒流失量
     */
    public double getWheatLossPerSecNow() {
        return baseWheatLoss * wheatLossMultiple * getTaskTeam().getInitSize();
    }

    private void startTiming() {
        subScheduler.runTaskTimer(() -> takeWheat(getWheatLossPerSecNow())
                , 20, 20);
        subScheduler.runTaskTimer(() -> addWheatLossMultiple(wheatLostAcceleratedSpeed)
                , 20 * 60 * 5L, 20 * 60 * 5L);
    }

    public double getHurtWheatCost() {
        return difficulty.getHurtWheatCost();
    }

    @Override
    public String getName() {
        return "自由勘探";
    }

    @Override
    public String getColor() {
        return "§f";
    }
    /**
     * 游戏结束检查
     * 如果本次任务玩家数为0则意味着所有玩家逃跑/离线，宣布任务失败
     * 如果撤离玩家数和任务玩家数一致，则任务完成
     */
    @Override
    protected void gameOverCheck() {
        {
            subScheduler.runTaskTimer(() -> {
                if (getTaskTeamSize() == 0) {
                    triggerFailed();
                    return;
                }
                if (waitForEvacuatePlayers().size() == getTaskTeamSize()) {
                    triggerFinish();
                }
            }, 20, 20);
        }
    }
    @Override
    public void start()  {
        if(getTaskRegion() == null) {
            Notifier.error("在任务区域未初始化时执行了任务的start方法");
            return;
        }
        initRecord();
        getTaskStat().enable();
        this.taskWheat = (double) getTaskTeamSize() * (getTaskDifficulty().getWheatCost() + getTaskDifficulty().getWheatSupply());
        getStrategyDecider().enable();
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {
            List<Location> portalLocations = IOC.getBean(BlockAPI.class)
                    .searchBlock(Material.END_PORTAL_FRAME, getTaskRegion().getCenter(), 30);
            setSpawnLocations(portalLocations);
            Bukkit.getScheduler().runTask(Siriuxa.getInstance(), () -> {
                // 战利品箱子数量
                int lootChestAmount = 0;
                // 生成初始战利品
                List<Location> chestLocations = IOC.getBean(BlockAPI.class).searchBlock(Material.CHEST, getTaskRegion().getCenter(), 30);
                for (Location location : chestLocations) {
                    if (location.getBlock().getType() != Material.CHEST) continue;
                    if (lootChestAmount >= getTaskTeamSize()) {
                        location.getBlock().setType(Material.AIR);
                        continue;
                    } // 跟人数有关
                    Chest chest = (Chest) location.getBlock().getState();
                    new ChestLoot(chest).applyLootTable();
                    lootChestAmount++;
                }

                for (Player player : getTaskPlayers()) {
                    IOC.getBean(TaskService.class).goTask(player, this);
                }
                gameOverCheck();
                startTiming();
                startEvacuateTask(random.nextInt(12, 20));
                getTaskRegion().startCheck();
            });
        });
    }

}
