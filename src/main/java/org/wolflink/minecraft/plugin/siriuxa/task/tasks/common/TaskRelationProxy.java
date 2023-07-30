package org.wolflink.minecraft.plugin.siriuxa.task.tasks.common;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.plugin.siriuxa.task.interfaces.ITaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.ExplorationTask;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.ExplorationTaskService;

@Singleton
public class TaskRelationProxy {

    @Inject
    private Config config;

    @NonNull
    public ITaskService getTaskService(Class<? extends Task> taskClass) {
        if(ExplorationTask.class.isAssignableFrom(taskClass)) return IOC.getBean(ExplorationTaskService.class);
        throw new IllegalArgumentException("暂不支持的任务类型："+taskClass.getName());
    }
    @NonNull
    public ITaskService getTaskService(Task task) {
        return getTaskService(task.getClass());
    }
    @Nullable
    public ITaskService getTaskService(String worldName) {
        if(worldName.equalsIgnoreCase(config.get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME))) return getTaskService(ExplorationTask.class);
        return null;
    }
    public Class<? extends TaskDifficulty> getTaskDifficultyClass(Class<? extends Task> taskClass) {
        if(ExplorationTask.class.isAssignableFrom(taskClass)) return ExplorationDifficulty.class;
        throw new IllegalArgumentException("暂不支持的任务类型："+taskClass.getName());
    }
    public Class<? extends TaskDifficulty> getTaskDifficultyClass(Task task) {
        return getTaskDifficultyClass(task.getClass());
    }
}