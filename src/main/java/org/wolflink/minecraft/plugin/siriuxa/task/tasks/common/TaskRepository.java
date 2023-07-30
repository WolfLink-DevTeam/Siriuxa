package org.wolflink.minecraft.plugin.siriuxa.task.tasks.common;

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

    public <T extends Task> T findByGlobalTeamPlayer(Class<T> taskClass, Player player) {
        return findByGlobalTeamPlayerUuid(taskClass, player.getUniqueId());
    }

    public <T extends Task> T findByGlobalTeamPlayerUuid(Class<T> taskClass, UUID uuid) {
        for (Task task : findAll()) {
            if (!task.getClass().equals(taskClass)) continue;
            if (task.globalTeamContains(uuid)) {
                return (T) task;
            }
        }
        return null;
    }

    @Nullable
    public Task findByGlobalTeamPlayer(Player player) {
        return findByGlobalTeamPlayerUuid(player.getUniqueId());
    }

    public Task findByGlobalTeamPlayerUuid(UUID uuid) {
        for (Task task : findAll()) {
            if (task.globalTeamContains(uuid)) {
                return task;
            }
        }
        return null;
    }

    @Nullable
    public <T extends Task> T findByTaskTeamPlayer(Class<T> taskClass, Player player) {
        return findByTaskTeamPlayerUuid(taskClass, player.getUniqueId());
    }

    public <T extends Task> T findByTaskTeamPlayerUuid(Class<T> taskClass, UUID uuid) {
        for (Task task : findAll()) {
            if (!task.getClass().equals(taskClass)) continue;
            if (task.taskTeamContains(uuid)) {
                return (T) task;
            }
        }
        return null;
    }

    @Nullable
    public Task findByTaskTeamPlayer(Player player) {
        return findByTaskTeamPlayerUuid(player.getUniqueId());
    }

    public Task findByTaskTeamPlayerUuid(UUID uuid) {
        for (Task task : findAll()) {
            if (task.taskTeamContains(uuid)) {
                return task;
            }
        }
        return null;
    }
}
