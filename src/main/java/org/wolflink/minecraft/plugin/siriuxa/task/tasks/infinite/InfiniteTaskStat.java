package org.wolflink.minecraft.plugin.siriuxa.task.tasks.infinite;

import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskStat;

public class InfiniteTaskStat extends TaskStat {
    InfiniteTask infiniteTask;
    /**
     * 子任务完成数量
     */
    int subTaskFinishedAmount = 0;
    public InfiniteTaskStat(InfiniteTask infiniteTask) {
        super(infiniteTask);
        this.infiniteTask = infiniteTask;
    }
}
