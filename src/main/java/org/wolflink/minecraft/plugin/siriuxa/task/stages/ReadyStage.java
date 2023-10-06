package org.wolflink.minecraft.plugin.siriuxa.task.stages;


public abstract class ReadyStage extends TaskStage {
    public ReadyStage(TaskLinearStageHolder stageHolder) {
        super("即将开始", stageHolder);
    }
}
