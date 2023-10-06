package org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration.stage;

import org.wolflink.minecraft.plugin.siriuxa.task.stages.EndStage;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskStage;

public class ExplorationEndStage extends EndStage {
    public ExplorationEndStage(TaskLinearStageHolder stageHolder) {
        super(stageHolder);
    }

    @Override
    protected void onEnter() {
        super.onEnter();
        //TODO 转移物资，传送回城等
    }

    @Override
    protected void onLeave() {
        super.onLeave();
    }
}
