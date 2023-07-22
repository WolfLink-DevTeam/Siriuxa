package org.wolflink.minecraft.plugin.siriuxa.task.common;

import lombok.Data;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.Calendar;

@Data
public class TaskStat {

    private final Task task;
    private final SubScheduler subScheduler = new SubScheduler();
    private Calendar startTime = null;
    private Calendar endTime = null;
    private double lastWheat = 0;
    private double nowWheat = 0;
    private boolean enabled = false;

    public double getWheatChange() {
        return nowWheat - lastWheat;
    }
    public final long getUsingTimeInMills() {
        if(endTime == null || startTime == null) return -1;
        return endTime.getTimeInMillis() - startTime.getTimeInMillis();
    }
    public TaskStat(Task task) {
        this.task = task;
    }

    public void setEnabled(boolean value) {
        if(enabled == value) return;
        enabled = value;
        if(enabled) {
            startTime = Calendar.getInstance();
            subScheduler.runTaskTimerAsync(() -> {
                lastWheat = nowWheat;
                nowWheat = task.getTaskWheat();
            }, 20, 20);
        } else {
            endTime = Calendar.getInstance();
            subScheduler.cancelAllTasks();
        }
    }
}
