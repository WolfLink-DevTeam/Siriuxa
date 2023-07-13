package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task;

import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.database.repository.MapRepository;

import javax.annotation.Nullable;
import java.util.UUID;

@Singleton
public class ExplorationTaskRepository extends MapRepository<Integer,ExplorationTask> {
    @Override
    public Integer getPrimaryKey(ExplorationTask explorationTask) {
        return explorationTask.getTaskId();
    }
    @Nullable
    public ExplorationTask findByPlayer(Player player) {
        return findByUuid(player.getUniqueId());
    }
    public ExplorationTask findByUuid(UUID uuid) {
        for (ExplorationTask task : findAll()) {
            if(task.getPlayerUuids().contains(uuid)) {
                return task;
            }
        }
        return null;
    }
}
