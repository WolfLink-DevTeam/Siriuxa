package org.wolflink.minecraft.plugin.siriuxa.task.stages;

public abstract class GameStage extends TaskStage {
    public GameStage(TaskLinearStageHolder stageHolder) {
        super("正在进行", stageHolder);
    }
}
