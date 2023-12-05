package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.stage;

import lombok.NonNull;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.BaseEndStage;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;

import java.util.function.Supplier;

public class EndStage extends BaseEndStage {
    public EndStage(TaskLinearStageHolder stageHolder) {
        super(stageHolder);
    }

    @Override
    protected @NonNull Supplier<Boolean> nextCondition() {
        return null;
    }

    @Override
    protected @NonNull Long nextCheckPeriodTick() {
        return null;
    }

    @Override
    protected void initScheduleTask() {

    }

    @Override
    protected void onEnter() {

    }

    @Override
    protected void onLeave() {

    }
}
