package org.wolflink.minecraft.plugin.siriuxa.task.interfaces;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeam;

import java.util.List;
import java.util.UUID;

public interface ITaskTeam {
    /**
     * 任务队伍(不可加入) 在任务预加载阶段初始化
     */
    TaskTeam getTaskTeam();
    default List<OfflinePlayer> getOfflinePlayers() {
        return getTaskTeam().getOfflinePlayers();
    }

    default int getTaskTeamSize() {
        return getTaskTeam().size();
    }

    default boolean taskTeamContains(UUID uuid) {
        return getTaskTeam().contains(uuid);
    }

    default boolean taskTeamContains(Player player) {
        return taskTeamContains(player.getUniqueId());
    }
    /**
     * 获取执行任务过程中的所有在线玩家
     */
    default List<Player> getTaskPlayers() {
        return getTaskTeam().getPlayers();
    }
}
