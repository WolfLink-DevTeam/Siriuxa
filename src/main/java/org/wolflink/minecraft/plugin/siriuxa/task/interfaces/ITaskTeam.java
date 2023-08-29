package org.wolflink.minecraft.plugin.siriuxa.task.interfaces;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ITaskTeam {
    /**
     * 任务队伍(不可加入) 在任务预加载阶段初始化
     */
    @Nullable
    TaskTeam getTaskTeam();

    default List<OfflinePlayer> getOfflinePlayers() {
        TaskTeam taskTeam = getTaskTeam();
        if (taskTeam == null) return new ArrayList<>();
        return taskTeam.getOfflinePlayers();
    }

    default int getTaskTeamSize() {
        TaskTeam taskTeam = getTaskTeam();
        if (taskTeam == null) return 0;
        return taskTeam.size();
    }

    default boolean taskTeamContains(UUID uuid) {
        TaskTeam taskTeam = getTaskTeam();
        if (taskTeam == null) return false;
        return taskTeam.contains(uuid);
    }

    default boolean taskTeamContains(Player player) {
        return taskTeamContains(player.getUniqueId());
    }

    /**
     * 获取执行任务过程中的所有在线玩家
     */
    default List<Player> getTaskPlayers() {
        TaskTeam taskTeam = getTaskTeam();
        if (taskTeam == null) return new ArrayList<>();
        return taskTeam.getPlayers();
    }
}
