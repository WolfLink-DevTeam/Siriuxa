package org.wolflink.minecraft.plugin.siriuxa.task.tasks.common;

import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;

import javax.annotation.Nullable;

@Singleton
public class TaskFactory {

    @Nullable
    public Task create(Class<? extends Task> taskClass, GlobalTeam globalTeam, TaskDifficulty taskDifficulty) {
        try {
            Task task = null;
            if (taskClass.getSimpleName().equals("ComposableTask")) {
                task = IOC.getBean(taskClass, globalTeam, taskDifficulty);
            }
            if (task == null) throw new IllegalArgumentException("不支持的任务类型：" + taskClass.getName());
            return task;
        } catch (Exception e) {
            Notifier.error("创建" + taskClass.getName() + "类型的任务失败。");
        }
        return null;
    }
}
