package org.wolflink.minecraft.plugin.siriuxa.team;

import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class TaskTeam extends Team {
    /**
     * 创建该任务队伍的源队伍UUID
     */
    @Getter
    private final UUID sourceTeamUuid;
    /**
     * 初始队伍大小
     */
    @Getter
    private final int initSize;

    public TaskTeam(GlobalTeam globalTeam) {
        super(globalTeam.getOwnerUuid(),new HashSet<>(globalTeam.getMemberUuids()));
        sourceTeamUuid = globalTeam.getTeamUuid();
        initSize = globalTeam.getMemberUuids().size();
    }

    @Override
    @SuppressWarnings("TaskTeam 不允许加入玩家")
    void join(Player player) {
    }

    @Override
    @SuppressWarnings("TaskTeam 不允许加入玩家")
    void join(UUID uuid) {
    }

    @Override
    public void leave(Player player) {
        leave(player.getUniqueId());
    }

    public void leave(OfflinePlayer offlinePlayer) {
        leave(offlinePlayer.getUniqueId());
    }
}
