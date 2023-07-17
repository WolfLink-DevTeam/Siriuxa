package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common;

import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.database.repository.MapRepository;

import javax.annotation.Nullable;
import java.util.UUID;

@Singleton
public class TaskRepository extends MapRepository<UUID, Task> {
    @Override
    public UUID getPrimaryKey(Task explorationTask) {
        return explorationTask.getTaskUuid();
    }

    @Nullable
    public <T extends Task> T findByPlayer(Class<T> taskClass, Player player) {
        return findByPlayerUuid(taskClass, player.getUniqueId());
    }

    @Nullable
    public Task findByPlayer(Player player) {
        return findByPlayerUuid(player.getUniqueId());
    }

    public Task findByPlayerUuid(UUID uuid) {
        for (Task task : findAll()) {
            if (task.getTaskTeam().contains(uuid)) {
                return task;
            }
        }
        return null;
    }

    public <T extends Task> T findByPlayerUuid(Class<T> taskClass, UUID uuid) {
        for (Task task : findAll()) {
            if (!task.getClass().equals(taskClass)) continue;
            if (task.getTaskTeam().contains(uuid)) {
                return (T) task;
            }
        }
        return null;
    }
}
