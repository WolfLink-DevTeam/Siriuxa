package org.wolflink.minecraft.plugin.siriuxa.task.tasks.common;

import lombok.Data;
import lombok.Getter;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.IStatus;
import org.wolflink.minecraft.plugin.siriuxa.task.listeners.StatListener;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用任务统计
 */
@Getter
public abstract class TaskStat implements IStatus {

    protected boolean enabled = false;
    protected final Task task;
    protected final SubScheduler subScheduler = new SubScheduler();
    protected Calendar startTime = null;
    protected Calendar endTime = null;
    protected final Map<UUID,Integer> travelDistanceMap = new ConcurrentHashMap<>();
    protected final Map<UUID,Integer> damageMap = new ConcurrentHashMap<>();
    protected final Map<UUID,Integer> oreSellMap = new ConcurrentHashMap<>();
    protected final Map<UUID,Integer> mobKillMap = new ConcurrentHashMap<>();

    public TaskStat(Task task) {
        this.task = task;
    }
    public final long getUsingTimeInMills() {
        if (endTime == null || startTime == null) return -1;
        return endTime.getTimeInMillis() - startTime.getTimeInMillis();
    }
    @Override
    public void enable() {
        enabled = true;
        startTime = Calendar.getInstance();
        IOC.getBean(StatListener.class).addStat(this);
    }
    @Override
    public void disable() {
        enabled = false;
        IOC.getBean(StatListener.class).removeStat(this);
        endTime = Calendar.getInstance();
        subScheduler.cancelAllTasks();
    }
}
