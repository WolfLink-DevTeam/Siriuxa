package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.timetrap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskAttribute;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskAttributeType;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskEndEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskLumenLeftNotifyEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskStartEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.ComposableTask;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.OrnamentType;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.HashSet;
import java.util.Set;

/**
 * 任务光体池
 */
@Singleton
public class TaskLumenPool extends WolfirdListener {
    private final SubScheduler subScheduler = new SubScheduler();
    private final Set<ComposableTask> availableTasks = new HashSet<>();

    @Override
    public void onEnable() {
        super.onEnable();
        subScheduler.runTaskTimerAsync(this::updateLumenPerSecond,20,20);
    }
    private void updateLumenPerSecond() {
        for (ComposableTask task : availableTasks) {
            TaskAttribute taskAttribute = task.getTaskAttribute();
            increaseTaskLumenLostMultiple(task,taskAttribute.getAttribute(TaskAttributeType.LUMEN_LOST_ACC_SPEED,0.0));
            double lostPerSecond = getTaskLumenLostPerSecondNow(task);
            double lumenLeft = getTaskLumen(task);
            double nowLumenLeft = lumenLeft - lostPerSecond;
            if(nowLumenLeft < 0) nowLumenLeft = 0;
            taskAttribute.setAttribute(TaskAttributeType.LUMEN_LEFT,nowLumenLeft);
            double delta = nowLumenLeft - taskAttribute.getAttribute(TaskAttributeType.LUMEN_LEFT_LAST_SECOND,0.0);
            taskAttribute.setAttribute(TaskAttributeType.LUMEN_LEFT_DELTA,delta);
            taskAttribute.setAttribute(TaskAttributeType.LUMEN_LEFT_LAST_SECOND,nowLumenLeft);
            // TODO 改为异步提示
//        int lumenTime = lumenTaskStat.getLumenTimeLeft();
//        if (0 < lumenTime && lumenTime <= 300 && status != TaskLumenLeftNotifyEvent.Status.FEW) {
//            status = TaskLumenLeftNotifyEvent.Status.FEW;
//            Bukkit.getPluginManager().callEvent(new TaskLumenLeftNotifyEvent(this, status));
//        } else if (300 < lumenTime && lumenTime <= 600 && status != TaskLumenLeftNotifyEvent.Status.INSUFFICIENT) {
//            status = TaskLumenLeftNotifyEvent.Status.INSUFFICIENT;
//            Bukkit.getPluginManager().callEvent(new TaskLumenLeftNotifyEvent(this, status));
//        } else if (600 < lumenTime && status != TaskLumenLeftNotifyEvent.Status.ENOUGH) {
//            status = TaskLumenLeftNotifyEvent.Status.ENOUGH;
//            Bukkit.getPluginManager().callEvent(new TaskLumenLeftNotifyEvent(this, status));
//        }
            if(nowLumenLeft == 0) task.getTaskLifeCycle().triggerFailed();
        }
    }

    /**
     * 增加任务光体流失倍率
     * @param task      任务
     * @param accSpeed  每秒增加的流失倍率(如 0.01 代表每秒增加 1% )
     */
    private void increaseTaskLumenLostMultiple(ComposableTask task,double accSpeed) {
        double lumenLossMultiple = task.getTaskAttribute().getAttribute(TaskAttributeType.LUMEN_LOST_MULTIPLE_NOW,0.0);
        lumenLossMultiple += accSpeed;
        task.getTaskAttribute().setAttribute(TaskAttributeType.LUMEN_LOST_MULTIPLE_NOW,lumenLossMultiple);
    }
    /**
     * 获取任务当前剩余光体
     */
    public double getTaskLumen(ComposableTask task) {
        if(!availableTasks.contains(task)) return 0;
        return task.getTaskAttribute().getAttribute(TaskAttributeType.LUMEN_LEFT,0.0);
    }
    private int calculateTime(double lumen, double baseLoss, double lossAcceleratedValue) {
        double totalMoneySpent = 0;
        int n = 0;
        while (totalMoneySpent <= lumen) {
            totalMoneySpent += (baseLoss + n * lossAcceleratedValue);
            n++;
        }
        totalMoneySpent -= (baseLoss + (n - 1) * lossAcceleratedValue);
        n--;
        double remainingMoney = lumen - totalMoneySpent;
        double remainingTime = remainingMoney / (baseLoss + n * lossAcceleratedValue);
        return (int) (n + remainingTime);
    }
    /**
     * 大致获取任务当前光体相应的生存时间
     */
    public double getLumenTimeLeft(ComposableTask task) {
        double nowLumen = getTaskLumen(task);
        double lossSpeed = getTaskLumenLostPerSecondNow(task);
        double accSpeed = task.getTaskAttribute().getAttribute(TaskAttributeType.LUMEN_LOST_ACC_SPEED,0.0);
        return calculateTime(nowLumen, lossSpeed, accSpeed);
    }

    /**
     * 获取光体当前每秒流失量
     */
    public double getTaskLumenLostPerSecondNow(ComposableTask task) {
        if(!availableTasks.contains(task)) return 0;
        double baseLumenLoss = task.getTaskAttribute().getAttribute(TaskAttributeType.BASE_LUMEN_LOSS,0.0);
        double lumenLossMultiple = task.getTaskAttribute().getAttribute(TaskAttributeType.LUMEN_LOST_MULTIPLE_NOW,0.0);
        return baseLumenLoss * lumenLossMultiple;
    }

    @EventHandler
    void taskStart(TaskStartEvent event) {
        Task task = event.getTask();
        if(!(task instanceof ComposableTask composableTask)) return; // 不是组合式任务
        if(!(composableTask.getOrnamentTypes().contains(OrnamentType.TIME_TRAP))) return; // 不是时间陷阱任务
        TaskAttribute taskAttribute = composableTask.getTaskAttribute();
        // 初始化光体数量和光体流失速度
        taskAttribute.setAttribute(TaskAttributeType.LUMEN_LEFT, taskAttribute.getAttribute(TaskAttributeType.LUMEN_SUPPLY,0) * task.getTaskTeamSize());
        // 添加任务到计数池中
        availableTasks.add(composableTask);
    }
    @EventHandler
    void taskEnd(TaskEndEvent event) {
        Task task = event.getTask();
        if(!(task instanceof ComposableTask composableTask)) return; // 不是组合式任务
        if(!(composableTask.getOrnamentTypes().contains(OrnamentType.TIME_TRAP))) return; // 不是时间陷阱任务
        // 将任务移出计数池中
        availableTasks.remove(composableTask);
    }
}
