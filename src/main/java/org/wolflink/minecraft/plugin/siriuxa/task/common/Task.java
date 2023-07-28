package org.wolflink.minecraft.plugin.siriuxa.task.common;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.INameable;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.world.BlockAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationCommandSender;
import org.wolflink.minecraft.plugin.siriuxa.api.world.RegionAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.world.WorldEditAPI;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.plugin.siriuxa.file.database.OfflinePlayerDB;
import org.wolflink.minecraft.plugin.siriuxa.file.database.OfflinePlayerRecord;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerTaskRecord;
import org.wolflink.minecraft.plugin.siriuxa.file.database.TaskRecordDB;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.loot.ChestLoot;
import org.wolflink.minecraft.plugin.siriuxa.monster.StrategyDecider;
import org.wolflink.minecraft.plugin.siriuxa.task.common.region.SquareRegion;
import org.wolflink.minecraft.plugin.siriuxa.task.common.region.TaskRegion;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeam;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.StageHolder;

import java.util.*;

/**
 * 抽象任务类
 * 接受需要支付麦穗
 * 麦穗会流失
 */
@Data
public abstract class Task implements INameable {

    /**
     * 任务过程中的调度器，在任务结束后其中的所有任务都会销毁
     */
    private final SubScheduler subScheduler = new SubScheduler();
    /**
     * 任务数据统计类(纯异步)
     */
    private final TaskStat taskStat = new TaskStat(this);
    /**
     * 任务UUID
     */
    private final UUID taskUuid = UUID.randomUUID();
    /**
     * 基础麦穗流失量(每秒)
     */
    private final double baseWheatLoss;
    /**
     * 麦穗流失加速度
     */
    private final double wheatLostAcceleratedSpeed;
    /**
     * 玩家队伍
     */
    @Getter
    private final GlobalTeam globalTeam;
    @NonNull
    private final TaskDifficulty taskDifficulty;
    private final Random random = new Random();
    @Getter
    private final StageHolder stageHolder;
    /**
     * 任务基础套装
     */
    private final PlayerBackpack defaultKit;
    /**
     * TODO 延迟初始化，刷怪决策者
     */
    private final StrategyDecider strategyDecider;
    private final Map<UUID, PlayerTaskRecord> playerRecordMap = new HashMap<>();
    /**
     * 麦穗流失倍率
     */
    private double wheatLossMultiple = 1.0;
    /**
     * 本次任务的麦穗余量
     */
    private double taskWheat = 0;
    /**
     * 任务队伍(不可加入) 在任务预加载阶段初始化
     */
    private TaskTeam taskTeam = new TaskTeam(new GlobalTeam());
    @Nullable
    private TaskRegion taskRegion = null;
    /**
     * 当前可用的撤离点
     */
    private EvacuationZone availableEvacuationZone = null;
    private List<Location> beaconLocations = new ArrayList<>();

    protected Task(GlobalTeam globalTeam, TaskDifficulty taskDifficulty, PlayerBackpack defaultKit) {
        this.globalTeam = globalTeam;
        this.taskDifficulty = taskDifficulty;
        this.wheatLostAcceleratedSpeed = taskDifficulty.getWheatLostAcceleratedSpeed();
        this.baseWheatLoss = taskDifficulty.getBaseWheatLoss();
        this.defaultKit = defaultKit;
        stageHolder = initStageHolder();
        strategyDecider = new StrategyDecider(this);
    }

    protected abstract StageHolder initStageHolder();

    public List<OfflinePlayer> getOfflinePlayers() {
        return taskTeam.getOfflinePlayers();
    }

    public int size() {
        return taskTeam.size();
    }

    public boolean taskTeamContains(UUID uuid) {
        return taskTeam.contains(uuid);
    }

    public boolean taskTeamContains(Player player) {
        return taskTeamContains(player.getUniqueId());
    }

    public boolean globalTeamContains(UUID uuid) {
        return globalTeam.contains(uuid);
    }

    public boolean globalTeamContains(Player player) {
        return globalTeamContains(player.getUniqueId());
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
     * 获取执行任务过程中的所有在线玩家
     */
    public List<Player> getTaskPlayers() {
        return taskTeam.getPlayers();
    }

    public void addWheatLossMultiple(double value) {
        wheatLossMultiple += value;
    }

    public Set<Player> waitForEvacuatePlayers() {
        if (availableEvacuationZone == null) return new HashSet<>();
        else return availableEvacuationZone.getPlayerInZone();
    }

    private void triggerFailed() {
        getTaskPlayers().forEach(player -> fillRecord(player, false));
        stageHolder.next();
        taskStat.setEnabled(false);
        stopCheck();
        finishRecord();
        failed();
        for (Player player : getGlobalTeam().getPlayers()) {
            IOC.getBean(TaskService.class).goLobby(player);
            Siriuxa.getInstance().getSubScheduler().runTaskLater(() -> {
                player.sendTitle("§c任务失败", "§7嘿！别灰心丧气的，下次加油！", 10, 80, 10);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.8f);
            }, 20 * 3L);
        }
        deleteTask();
    }

    private void triggerFinish() {
        getTaskPlayers().forEach(player -> fillRecord(player, true));
        stageHolder.next();
        taskStat.setEnabled(false);
        stopCheck();
        finishRecord();
        finish();
        for (Player player : getGlobalTeam().getPlayers()) {
            IOC.getBean(TaskService.class).goLobby(player);
            Siriuxa.getInstance().getSubScheduler().runTaskLater(() -> {
                player.sendTitle("§a任务完成", "§7前往领取本次任务的报酬吧", 10, 80, 10);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
            }, 20 * 3L);
        }
        deleteTask();
    }

    /**
     * 游戏结束检查
     * 如果本次任务玩家数为0则意味着所有玩家逃跑/离线，宣布任务失败
     * 如果撤离玩家数和任务玩家数一致，则任务完成
     */
    public void startGameOverCheck() {
        subScheduler.runTaskTimer(() -> {
            if (size() == 0) {
                triggerFailed();
                return;
            }
            if (waitForEvacuatePlayers().size() == size()) {
                triggerFinish();
            }
        }, 20, 20);
    }

    public void preLoad() {
        this.taskTeam = new TaskTeam(getGlobalTeam());
        subScheduler.runTaskLaterAsync(() -> {
            String worldName = IOC.getBean(Config.class).get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME);
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                Notifier.error(worldName + "世界不存在！请检查配置文件");
                return;
            }
            Location regionCenter = IOC.getBean(RegionAPI.class).autoGetRegionCenter(world);
            this.taskRegion = new SquareRegion(this, regionCenter);
            IOC.getBean(WorldEditAPI.class).pasteWorkingUnit(new LocationCommandSender(taskRegion.getCenter().clone().add(0, 2, 0)));
        }, 0);
    }

    public void start() {
        initRecord();
        taskStat.setEnabled(true);
        this.taskWheat = (double) size() * (taskDifficulty.getWheatCost() + taskDifficulty.getWheatSupply());
        strategyDecider.enable();
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {

            beaconLocations = IOC.getBean(BlockAPI.class).searchBlock(Material.END_PORTAL_FRAME, taskRegion.getCenter(), 30);
            Bukkit.getScheduler().runTask(Siriuxa.getInstance(), () -> {
                // 战利品箱子数量
                int lootChestAmount = 0;
                // 生成初始战利品
                List<Location> chestLocations = IOC.getBean(BlockAPI.class).searchBlock(Material.CHEST, taskRegion.getCenter(), 30);
                for (Location location : chestLocations) {
                    if (location.getBlock().getType() != Material.CHEST) continue;
                    if (lootChestAmount >= size()) {
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
                startGameOverCheck();
                startTiming();
                startEvacuateTask(random.nextInt(12, 20));
                taskRegion.startCheck();
            });
        });
    }

    private void startEvacuateTask(int minutes) {
        subScheduler.runTaskLater(() -> {
            if (taskRegion == null) return;
            Location evacuateLocation = taskRegion.getEvacuateLocation((int) taskRegion.getRadius());
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
     * 获取当前麦穗每秒流失量
     */
    public double getWheatLossPerSecNow() {
        return baseWheatLoss * wheatLossMultiple * taskTeam.getInitSize();
    }

    private void startTiming() {
        subScheduler.runTaskTimer(() -> takeWheat(getWheatLossPerSecNow())
                , 20, 20);
        subScheduler.runTaskTimer(() -> addWheatLossMultiple(wheatLostAcceleratedSpeed)
                , 20 * 60 * 5L, 20 * 60 * 5L);
    }

    private void stopCheck() {
        strategyDecider.disable();
        subScheduler.cancelAllTasks();
        if (taskRegion != null) {
            taskRegion.stopCheck();
            taskRegion = null;
        }
    }

    /**
     * 任务玩家全部撤离时任务完成
     */
    protected abstract void finish();

    /**
     * 麦穗为0，或玩家全部逃跑时，任务失败
     */
    public abstract void failed();

    /**
     * 清理本次任务
     * 在任务完成/失败后调用
     */
    protected void deleteTask() {
        if (globalTeam != null) globalTeam.setSelectedTask(null);
        taskTeam.clear();
        IOC.getBean(TaskRepository.class).deleteByKey(taskUuid);
    }

    /**
     * 初始化任务快照
     */
    private void initRecord() {
        for (UUID uuid : taskTeam.getMemberUuids()) {
            PlayerTaskRecord record = new PlayerTaskRecord(uuid, this);
            playerRecordMap.put(uuid, record);
        }
    }

    /**
     * 填充玩家任务快照
     */
    private void fillRecord(OfflinePlayer offlinePlayer, boolean taskResult) {
        PlayerTaskRecord record = playerRecordMap.get(offlinePlayer.getUniqueId());
        if (record == null) {
            Notifier.error("在尝试补充任务记录数据时，未找到玩家" + offlinePlayer.getName() + "的任务记录类。");
            return;
        }
        record.setWheat(taskWheat / taskTeam.getInitSize()); // 保存任务麦穗
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
    private void finishRecord() {
        TaskRecordDB taskRecordDB = IOC.getBean(TaskRecordDB.class);
        for (PlayerTaskRecord playerTaskRecord : playerRecordMap.values()) {
            playerTaskRecord.setUsingTimeInMills(taskStat.getUsingTimeInMills());
            playerTaskRecord.setFinishedTimeInMills(taskStat.getEndTime().getTimeInMillis());
            taskRecordDB.saveRecord(playerTaskRecord);
        }
    }

    /**
     * 撤离玩家
     * (适用于只有部分玩家乘坐撤离飞艇的情况)
     */
    public void evacuate(Player player) {
        fillRecord(player, true);
        taskTeam.leave(player);
        Notifier.debug("玩家" + player.getName() + "在任务中先一步撤离了。");
        Notifier.broadcastChat(taskTeam.getPlayers(), "玩家" + player.getName() + "已乘坐飞艇撤离。");
        player.teleport(IOC.getBean(Config.class).getLobbyLocation());
        Siriuxa.getInstance().getSubScheduler().runTaskLater(() -> {
            player.sendTitle("§a任务完成", "§7等待任务完全结束后方可领取报酬", 10, 80, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
        }, 20 * 3L);
    }

    public void death(Player player) {
        fillRecord(player, false);
        taskTeam.leave(player);
        player.setGameMode(GameMode.SPECTATOR);
        Notifier.debug("玩家" + player.getName() + "在任务中阵亡了。");
        Notifier.broadcastChat(taskTeam.getPlayers(), "玩家" + player.getName() + "在任务中阵亡了。");
        player.sendTitle("§c§l寄！", "§7胜败乃兵家常事，大侠请重新来过。", 10, 80, 10);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 0.5f);
        if (taskTeam.isEmpty()) triggerFailed();
    }

    /**
     * 玩家逃跑
     * (适用于任务过程中玩家非正常离开任务的情况)
     */
    public void escape(OfflinePlayer offlinePlayer) {
        fillRecord(offlinePlayer, false);
        taskTeam.leave(offlinePlayer);
        Notifier.debug("玩家" + offlinePlayer.getName() + "在任务过程中失踪了。");
        Notifier.broadcastChat(taskTeam.getPlayers(), "玩家" + offlinePlayer.getName() + "在任务过程中失踪了。");
    }
}
