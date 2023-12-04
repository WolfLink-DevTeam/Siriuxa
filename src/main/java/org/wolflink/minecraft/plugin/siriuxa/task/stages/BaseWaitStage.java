package org.wolflink.minecraft.plugin.siriuxa.task.stages;

public abstract class BaseWaitStage extends TaskStage {
    public BaseWaitStage(TaskLinearStageHolder stageHolder) {
        super("等待中", stageHolder);
    }
}
