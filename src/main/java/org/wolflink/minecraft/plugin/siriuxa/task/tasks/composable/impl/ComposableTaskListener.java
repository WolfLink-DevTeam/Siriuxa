package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.impl;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskListener;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;

public class ComposableTaskListener extends TaskListener {

    public ComposableTaskListener(Task task) {
        super(task);
    }

    @Override
    public void onPlayerOnline(Player player) {

    }

    @Override
    public void onPlayerOffline(OfflinePlayer offlinePlayer) {

    }

    /**
     * 玩家死亡
     */
    @Override
    public void onPlayerDeath(Player player) {
        if (task.getTaskTeam() == null) {
            Notifier.error("任务的队伍未被初始化！");
            return;
        }
        task.getTaskRecorder().fillRecord(player, false);
        task.getTaskTeam().leave(player);
        player.setGameMode(GameMode.SPECTATOR);
        Notifier.debug("玩家" + player.getName() + "在任务中阵亡了。");
        Notifier.broadcastChat(task.getTaskTeam().getPlayers(), "玩家" + player.getName() + "在任务中阵亡了。");
        player.sendTitle("§c§l寄！", "§7胜败乃兵家常事，大侠请重新来过。", 10, 80, 10);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 0.5f);
        if (task.getTaskTeam().isEmpty()) task.getTaskLifeCycle().triggerFailed();
    }

    /**
     * 玩家逃跑
     * (适用于任务过程中玩家非正常离开任务的情况)
     */
    @Override
    public void onPlayerEscape(OfflinePlayer offlinePlayer) {
        if (task.getTaskTeam() == null) {
            Notifier.error("任务的队伍未被初始化！");
            return;
        }
        task.getTaskRecorder().fillRecord(offlinePlayer, false);
        task.getTaskTeam().leave(offlinePlayer);
        Notifier.debug("玩家" + offlinePlayer.getName() + "在任务过程中失踪了。");
        Notifier.broadcastChat(task.getTaskTeam().getPlayers(), "玩家" + offlinePlayer.getName() + "在任务过程中失踪了。");
    }
}
