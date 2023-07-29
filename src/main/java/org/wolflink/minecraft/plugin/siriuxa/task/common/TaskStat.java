package org.wolflink.minecraft.plugin.siriuxa.task.common;

import lombok.Data;
import org.wolflink.minecraft.plugin.siriuxa.api.IStatus;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.Calendar;

/**
 * 通用任务统计
 */
@Data
public abstract class TaskStat implements IStatus {

    protected final Task task;
    protected final SubScheduler subScheduler = new SubScheduler();
    protected Calendar startTime = null;
    protected Calendar endTime = null;

    public TaskStat(Task task) {
        this.task = task;
    }
    public final long getUsingTimeInMills() {
        if (endTime == null || startTime == null) return -1;
        return endTime.getTimeInMillis() - startTime.getTimeInMillis();
    }
    @Override
    public void enable() {
        startTime = Calendar.getInstance();
    }
    @Override
    public void disable() {
        endTime = Calendar.getInstance();
        subScheduler.cancelAllTasks();
    }
}
