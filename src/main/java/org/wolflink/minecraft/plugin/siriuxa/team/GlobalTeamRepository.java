package org.wolflink.minecraft.plugin.siriuxa.team;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.database.repository.MapRepository;

import javax.annotation.Nullable;
import java.util.UUID;

@Singleton
public class GlobalTeamRepository extends MapRepository<UUID, GlobalTeam> {
    @Override
    public UUID getPrimaryKey(GlobalTeam globalTeam) {
        return globalTeam.getTeamUuid();
    }

    @Nullable
    public GlobalTeam findByPlayerUuid(UUID uuid) {
        for (GlobalTeam globalTeam : findAll()) {
            if (globalTeam.contains(uuid)) return globalTeam;
        }
        return null;
    }

    @Nullable
    public GlobalTeam findByPlayer(Player player) {
        return findByPlayerUuid(player.getUniqueId());
    }
    @Nullable
    public GlobalTeam findByPlayer(OfflinePlayer offlinePlayer) {
        return findByPlayerUuid(offlinePlayer.getUniqueId());
    }
}
