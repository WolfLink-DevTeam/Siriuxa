package org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration;

import lombok.Getter;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.world.BlockAPI;
import org.wolflink.minecraft.plugin.siriuxa.backpack.InvBackupService;
import org.wolflink.minecraft.plugin.siriuxa.backpack.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.loot.ChestLoot;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskEndEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskStartEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.ornaments.OrnamentType;
import org.wolflink.minecraft.plugin.siriuxa.task.regions.EvacuationZone;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskProperties;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.lumen.LumenTask;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 自由勘探任务
 * 玩家需要乘坐飞艇撤离才算任务完成
 * 可携带物资离开
 */
public class ExplorationTask extends LumenTask {
    @Getter
    private final ExplorationDifficulty explorationDifficulty;
    /**
     * 当前可用的撤离点
     */
    @Getter
    private EvacuationZone availableEvacuationZone = null;
    public ExplorationTask(GlobalTeam globalTeam, ExplorationDifficulty explorationDifficulty) {
        super(globalTeam, explorationDifficulty);
        this.explorationDifficulty = explorationDifficulty;
    }
    public Set<Player> waitForEvacuatePlayers() {
        if (availableEvacuationZone == null) return new HashSet<>();
        else return availableEvacuationZone.getPlayerInZone();
    }
    private void startEvacuateTask(int minutes) {
        subScheduler.runTaskLater(() -> {
            if (getTaskArea() == null) return;
            Location evacuateLocation = getTaskArea().getEvacuateLocation((int) getTaskArea().getRadius());
            if (evacuateLocation == null) {
                triggerFailed();
                return;
            }
            if (availableEvacuationZone != null) {
                Notifier.broadcastChat(getTaskPlayers(), "飞艇将在 3分钟 后撤离，请抓紧时间！");
                subScheduler.runTaskLater(() -> {
                    Notifier.broadcastSound(getTaskPlayers(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 0.8f);
                    availableEvacuationZone.setAvailable(false);
                    availableEvacuationZone = null;
                    startEvacuateTask(random.nextInt(12, 20));
                }, 20 * 180L);
            } else {
                availableEvacuationZone = new EvacuationZone(this, evacuateLocation.getWorld(), evacuateLocation.getBlockX(), evacuateLocation.getBlockZ(), 30);
                availableEvacuationZone.setAvailable(true);
                startEvacuateTask(random.nextInt(12, 20));
            }
        }, 20L * 60 * minutes);
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
    private static final Set<OrnamentType> ornamentTypes = new HashSet<>();
    static {
        ornamentTypes.add(OrnamentType.SCULK_INFECTION);
        ornamentTypes.add(OrnamentType.SAFE_WORKING);
        ornamentTypes.add(OrnamentType.SUPPLIES_COLLECTION);
        ornamentTypes.add(OrnamentType.SMART_AI);
    }
    @Override
    public Set<OrnamentType> getOrnamentTypes() {
        return ornamentTypes;
    }
    @Override
    protected void finishedCheck() {
        subScheduler.runTaskTimer(() -> {
            if (waitForEvacuatePlayers().size() == getTaskTeamSize()) {
                triggerFinish();
            }
        }, 20, 20);
    }
    @Override
    protected void implPreLoad() {
        if (getTaskArea() == null) {
            Notifier.error("在任务区域未初始化时执行了任务的implPreLoad方法");
            return;
        }
        List<Location> portalLocations = IOC.getBean(BlockAPI.class)
                .searchBlock(Material.END_PORTAL_FRAME, getTaskArea().getCenter(), 30);
        setSpawnLocations(portalLocations);
        // 战利品箱子数量
        int lootChestAmount = 0;
        // 生成初始战利品
        List<Location> chestLocations = IOC.getBean(BlockAPI.class).searchBlock(Material.CHEST, getTaskArea().getCenter(), 30);
        for (Location location : chestLocations) {
            if (location.getBlock().getType() != Material.CHEST) continue;
            if (lootChestAmount >= getTaskTeamSize()) {
                subScheduler.runTask(() -> location.getBlock().setType(Material.AIR));
                continue;
            } // 跟人数有关
            subScheduler.runTask(() -> {
                Chest chest = (Chest) location.getBlock().getState();
                new ChestLoot(chest).applyLootTable();
            });
            lootChestAmount++;
        }
        finishPreLoad = true;
    }
    @Override
    public void start() {
        super.lumenTip.setEnabled(true);
        if (getTaskArea() == null) {
            Notifier.error("在任务区域未初始化时执行了任务的start方法");
            return;
        }
        initRecord();
        getTaskStat().enable();
        this.taskLumen = (double) getTaskTeamSize() * getExplorationDifficulty().getLumenSupply();
        getStrategyDecider().enable();
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {
            Bukkit.getScheduler().runTask(Siriuxa.getInstance(), () -> {
                for (Player player : getTaskPlayers()) {
                    IOC.getBean(ExplorationTaskService.class).goTask(player, this);
                }
                failedCheck();
                finishedCheck();
                startLumenTask();
                startEvacuateTask(random.nextInt(12, 20));
                getTaskArea().startCheck();
            });
            Bukkit.getScheduler().runTask(Siriuxa.getInstance(),()->Bukkit.getPluginManager().callEvent(new TaskStartEvent(this)));
        });
    }
    @Override
    protected void finish() {
        super.lumenTip.setEnabled(false);
        Bukkit.getPluginManager().callEvent(new TaskEndEvent(this,true));
    }
    @Override
    public void failed() {
        super.lumenTip.setEnabled(false);
        Bukkit.getPluginManager().callEvent(new TaskEndEvent(this,false));
    }
}
