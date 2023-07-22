package org.wolflink.minecraft.plugin.siriuxa.team;

import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.database.repository.MapRepository;

import javax.annotation.Nullable;
import java.util.UUID;

@Singleton
public class TeamRepository extends MapRepository<UUID, Team> {
    @Override
    public UUID getPrimaryKey(Team team) {
        return team.getTeamUuid();
    }

    @Nullable
    public Team findByPlayerUuid(UUID uuid) {
        for (Team team : findAll()) {
            if (team.contains(uuid)) return team;
        }
        return null;
    }

    @Nullable
    public Team findByPlayer(Player player) {
        return findByPlayerUuid(player.getUniqueId());
    }
}
