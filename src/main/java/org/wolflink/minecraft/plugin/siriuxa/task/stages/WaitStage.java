package org.wolflink.minecraft.plugin.siriuxa.task.stages;

public abstract class WaitStage extends TaskStage {
    public WaitStage(TaskLinearStageHolder stageHolder) {
        super("等待中", stageHolder);
    }
}
