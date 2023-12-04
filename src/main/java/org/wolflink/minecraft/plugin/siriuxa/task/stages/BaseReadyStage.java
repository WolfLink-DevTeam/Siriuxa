package org.wolflink.minecraft.plugin.siriuxa.task.stages;


public abstract class BaseReadyStage extends TaskStage {
    public BaseReadyStage(TaskLinearStageHolder stageHolder) {
        super("即将开始", stageHolder);
    }
}
