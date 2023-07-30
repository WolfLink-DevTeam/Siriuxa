package org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.taskstage;

import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskStage;

public class EndStage extends TaskStage {
    public EndStage(TaskLinearStageHolder stageHolder) {
        super("已结束", stageHolder);
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
