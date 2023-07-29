package org.wolflink.minecraft.plugin.siriuxa.task.common.interfaces;

import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.WheatTaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;

public interface ITaskService {
    void goLobby(Player player);

    void goTask(Player player, Task task);

    Result create(Player player, Class<? extends Task> taskClass, TaskDifficulty taskDifficulty);
    Result create(GlobalTeam globalTeam, Class<? extends Task> taskClass, TaskDifficulty taskDifficulty);
    Result ready(Task task);
    /**
     * 玩家能否接受该类型任务
     */
    boolean canAccept(Class<? extends Task> taskClass,TaskDifficulty taskDifficulty,OfflinePlayer offlinePlayer);
    /**
     * 接受任务，上交道具/麦穗等
     */
    void accept(Class<? extends Task> taskClass,TaskDifficulty taskDifficulty,OfflinePlayer offlinePlayer);
}
