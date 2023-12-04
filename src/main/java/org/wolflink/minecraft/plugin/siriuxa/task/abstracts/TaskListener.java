package org.wolflink.minecraft.plugin.siriuxa.task.abstracts;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;

public abstract class TaskListener {

    protected final Task task;
    public TaskListener(Task task) {
        this.task = task;
    }

    public abstract void onPlayerOnline(Player player);
    public abstract void onPlayerOffline(OfflinePlayer offlinePlayer);
    /**
     * 玩家死亡
     */
    public abstract void onPlayerDeath(Player player);
    /**
     * 玩家逃跑
     * (适用于任务过程中玩家非正常离开任务的情况)
     */
    public abstract void onPlayerEscape(OfflinePlayer offlinePlayer);
}
