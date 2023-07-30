package org.wolflink.minecraft.plugin.siriuxa.task.stages;

import lombok.Getter;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.LinearStageHolder;

public class TaskLinearStageHolder extends LinearStageHolder {
    @Getter
    private final Task task;

    public TaskLinearStageHolder(Task task) {
        super(false);
        this.task = task;
    }
}
