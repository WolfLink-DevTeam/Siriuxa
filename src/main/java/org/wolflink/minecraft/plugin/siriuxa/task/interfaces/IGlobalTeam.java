package org.wolflink.minecraft.plugin.siriuxa.task.interfaces;

import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;

import java.util.UUID;

public interface IGlobalTeam {
    GlobalTeam getGlobalTeam();

    default boolean globalTeamContains(UUID uuid) {
        return getGlobalTeam().contains(uuid);
    }

    default boolean globalTeamContains(Player player) {
        return globalTeamContains(player.getUniqueId());
    }
}
