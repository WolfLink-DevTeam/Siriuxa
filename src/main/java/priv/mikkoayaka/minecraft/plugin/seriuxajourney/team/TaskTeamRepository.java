package priv.mikkoayaka.minecraft.plugin.seriuxajourney.team;

import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.database.repository.MapRepository;

import javax.annotation.Nullable;
import java.util.UUID;

@Singleton
public class TaskTeamRepository extends MapRepository<UUID,TaskTeam> {
    @Override
    public UUID getPrimaryKey(TaskTeam taskTeam) {
        return taskTeam.getTeamUuid();
    }
    @Nullable
    public TaskTeam findByPlayerUuid(UUID uuid) {
        for (TaskTeam taskTeam : findAll()) {
            if(taskTeam.contains(uuid))return taskTeam;
        }
        return null;
    }
    @Nullable
    public TaskTeam findByPlayer(Player player) {
        return findByPlayerUuid(player.getUniqueId());
    }
}
