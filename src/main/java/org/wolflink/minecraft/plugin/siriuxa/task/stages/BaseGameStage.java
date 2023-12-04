package org.wolflink.minecraft.plugin.siriuxa.task.stages;

public abstract class BaseGameStage extends TaskStage {
    public BaseGameStage(TaskLinearStageHolder stageHolder) {
        super("正在进行", stageHolder);
    }
}
