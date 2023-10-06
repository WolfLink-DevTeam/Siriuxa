package org.wolflink.minecraft.plugin.siriuxa.task.stages;

import lombok.Getter;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.LinearStageHolder;

@Getter
public class TaskLinearStageHolder extends LinearStageHolder {
    private final Task task;

    public TaskLinearStageHolder(Task task) {
        super(false);
        this.task = task;
    }
}
