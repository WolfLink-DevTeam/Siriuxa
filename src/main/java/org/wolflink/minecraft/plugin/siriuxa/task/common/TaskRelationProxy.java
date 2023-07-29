package org.wolflink.minecraft.plugin.siriuxa.task.common;

import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.task.common.interfaces.ITaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.ExplorationTask;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.ExplorationTaskService;

@Singleton
public class TaskRelationProxy {
    public ITaskService getTaskService(Class<? extends Task> taskClass) {
        if(ExplorationTask.class.isAssignableFrom(taskClass)) return IOC.getBean(ExplorationTaskService.class);
        throw new IllegalArgumentException("暂不支持的任务类型："+taskClass.getName());
    }
    public ITaskService getTaskService(Task task) {
        return getTaskService(task.getClass());
    }
    public Class<? extends TaskDifficulty> getTaskDifficultyClass(Class<? extends Task> taskClass) {
        if(ExplorationTask.class.isAssignableFrom(taskClass)) return ExplorationDifficulty.class;
        throw new IllegalArgumentException("暂不支持的任务类型："+taskClass.getName());
    }
    public Class<? extends TaskDifficulty> getTaskDifficultyClass(Task task) {
        return getTaskDifficultyClass(task.getClass());
    }
}
