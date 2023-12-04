package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskAttribute;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskAttributeType;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskStat;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskProperties;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.environments.EnvironmentType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.goals.GoalType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.OrnamentType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.impl.ComposableTaskLifeCycle;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.impl.ComposableTaskListener;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.impl.ComposableTaskRecorder;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.impl.ComposableTaskRegion;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.stage.EndStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.stage.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.stage.ReadyStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.stage.WaitStage;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;

import java.util.Map;
import java.util.Set;

@Getter
public class ComposableTask extends Task {
    private final Set<OrnamentType> ornamentTypes;
    private final EnvironmentType environmentType;
    private final GoalType goalType;

    protected ComposableTask(
            @NotNull GlobalTeam globalTeam,
            @NotNull TaskDifficulty taskDifficulty,
            @NotNull Set<OrnamentType> ornamentTypes,
            @NotNull EnvironmentType environmentType,
            @NotNull GoalType goalType
    ) {
        super(globalTeam, taskDifficulty, "自由勘探", "§e");
        TaskLinearStageHolder linearStageHolder = new TaskLinearStageHolder(this);
        linearStageHolder.bindStages(new Stage[]{
                new WaitStage(linearStageHolder),
                new ReadyStage(linearStageHolder),
                new GameStage(linearStageHolder),
                new EndStage(linearStageHolder)
        });
        // 进入等待阶段
        linearStageHolder.next();
        // 初始化
        init(
                new ComposableTaskLifeCycle(this),
                new ComposableTaskRecorder(this),
                new TaskStat(this),
                new TaskAttribute(this),
                new ComposableTaskListener(this),
                new ComposableTaskRegion(this),
                linearStageHolder
        );
        this.ornamentTypes = ornamentTypes;
        this.environmentType = environmentType;
        this.goalType = goalType;
    }
}