package org.wolflink.minecraft.plugin.siriuxa.task.stages;

import lombok.Getter;
import lombok.NonNull;
import org.checkerframework.checker.units.qual.Time;
import org.jetbrains.annotations.NotNull;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import oshi.util.tuples.Pair;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Getter
public abstract class TaskStage extends Stage {

    private final TaskLinearStageHolder stageHolder;

    protected TaskStage(String displayName, TaskLinearStageHolder stageHolder) {
        super(displayName, stageHolder);
        this.stageHolder = stageHolder;
    }
    @NonNull
    abstract protected Supplier<Boolean> nextCondition();
    @NonNull
    abstract protected Long nextCheckPeriodTick();
    abstract protected void initScheduleTask();
}
