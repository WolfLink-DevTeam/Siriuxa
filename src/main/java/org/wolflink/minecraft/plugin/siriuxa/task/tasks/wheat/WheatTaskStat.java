package org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat;

import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskStat;

/**
 * 麦穗任务统计
 */
public class WheatTaskStat extends TaskStat {
    private double lastWheat = 0;
    private double nowWheat = 0;

    private final WheatTask wheatTask;
    public WheatTaskStat(WheatTask wheatTask) {
        super(wheatTask);
        this.wheatTask = wheatTask;
    }

    public double getWheatChange() {
        return nowWheat - lastWheat;
    }

    @Override
    public void enable() {
        super.enable();
        subScheduler.runTaskTimerAsync(() -> {
            lastWheat = nowWheat;
            nowWheat = wheatTask.getTaskWheat();
        }, 20, 20);
    }
}
