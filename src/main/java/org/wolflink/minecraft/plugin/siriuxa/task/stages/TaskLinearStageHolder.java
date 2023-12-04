package org.wolflink.minecraft.plugin.siriuxa.task.stages;

import lombok.Getter;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.LinearStageHolder;

@Getter
public class TaskLinearStageHolder extends LinearStageHolder {
    private final Task task;
    private final SubScheduler subScheduler;
    public TaskLinearStageHolder(Task task) {
        super(false);
        this.task = task;
        this.subScheduler = new SubScheduler();
    }
    private TaskStage getTaskStage() {
        if(thisStage == null) return null;
        else return (TaskStage) thisStage;
    }

    @Override
    public void next() {
        beforeNext();
        super.next();
        afterNext();
    }

    /**
     * 在任务切换到下一阶段之前触发
     */
    private void beforeNext() {
        if(thisStage == null) return;
        getThisStage().getSubScheduler().cancelAllTasks();
        subScheduler.cancelAllTasks();
        Notifier.debug("任务" + getTask().getTaskUuid().toString() + "离开" + thisStage.getDisplayName() + "阶段");
    }

    /**
     * 在任务切换到下一阶段后触发
     */
    private void afterNext() {
        TaskStage taskStage = getTaskStage();
        if(taskStage == null) return;
        // 初始化调度任务
        taskStage.initScheduleTask();
        // 开始下一阶段检查
        subScheduler.runTaskTimer(()->{
            Boolean result = taskStage.nextCondition().get();
            if(result) next();
        },taskStage.nextCheckPeriodTick(),taskStage.nextCheckPeriodTick());
        Notifier.debug("任务" + getTask().getTaskUuid().toString() + "进入" + thisStage.getDisplayName() + "阶段");
    }
}
