package org.wolflink.minecraft.plugin.siriuxa.task.common;

import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.ExplorationTask;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeam;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import javax.annotation.Nullable;

@Singleton
public class TaskFactory {

    @Nullable
    public Task create(Class<? extends Task> taskClass, TaskTeam taskTeam, TaskDifficulty taskDifficulty) {
        try {
            Task task = null;
            if(taskClass.equals(ExplorationTask.class)) {
                ExplorationDifficulty difficulty = (ExplorationDifficulty) taskDifficulty;
                task = IOC.getBean(taskClass,taskTeam, difficulty);
            }
            if(task == null) throw new IllegalArgumentException("不支持的任务类型："+taskClass.getName());
            return task;
        } catch (Exception e) {
            Notifier.error("创建"+taskClass.getName()+"类型的任务失败。");
        }
        return null;
    }
}
