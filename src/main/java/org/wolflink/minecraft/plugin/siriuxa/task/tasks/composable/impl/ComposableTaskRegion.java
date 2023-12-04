package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskRegion;
import org.wolflink.minecraft.plugin.siriuxa.task.regions.EvacuationZone;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.ComposableTask;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 可组合任务区域管理
 * 包含出生点区域、撤离区域
 */
public class ComposableTaskRegion extends TaskRegion {
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final ComposableTask task;
    private SubScheduler getScheduler() {
        return task.getSubScheduler();
    }

    public ComposableTaskRegion(ComposableTask task) {
        this.task = task;
    }

    /**
     * 当前可用的撤离点
     */
    @Getter
    private EvacuationZone availableEvacuationZone = null;

    public Set<Player> getEvacuablePlayers() {
        if (availableEvacuationZone == null) return new HashSet<>();
        else return availableEvacuationZone.getPlayerInZone();
    }

    public void startEvacuateTask(int minutes) {
        getScheduler().runTaskLater(() -> {
            if (taskArea == null) return;
            Location evacuateLocation = taskArea.findEvacuateLocation((int) taskArea.getRadius());
            if (evacuateLocation == null) {
                task.getTaskLifeCycle().triggerFailed();
                return;
            }
            if (availableEvacuationZone != null) {
                Notifier.broadcastChat(task.getTaskPlayers(), "飞艇将在 3分钟 后撤离，请抓紧时间！");
                getScheduler().runTaskLater(() -> {
                    Notifier.broadcastSound(task.getTaskPlayers(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 0.8f);
                    availableEvacuationZone.setAvailable(false);
                    availableEvacuationZone = null;
                    startEvacuateTask(random.nextInt(12, 20));
                }, 20 * 180L);
            } else {
                availableEvacuationZone = new EvacuationZone(task, evacuateLocation.getWorld(), evacuateLocation.getBlockX(), evacuateLocation.getBlockZ(), 30);
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
        task.getTaskRecorder().fillRecord(player, true);
        task.getTaskTeam().leave(player);
        Notifier.debug("玩家" + player.getName() + "在任务中先一步撤离了。");
        Notifier.broadcastChat(task.getTaskTeam().getPlayers(), "玩家" + player.getName() + "已乘坐飞艇撤离。");
        player.teleport(IOC.getBean(Config.class).getLobbyLocation());
        Siriuxa.getInstance().getSubScheduler().runTaskLater(() -> {
            player.sendTitle("§a任务完成", "§7等待任务完全结束后方可领取报酬", 10, 80, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
        }, 20 * 3L);
    }
}
