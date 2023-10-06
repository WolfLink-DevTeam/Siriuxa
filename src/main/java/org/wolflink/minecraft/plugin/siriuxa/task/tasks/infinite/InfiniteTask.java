package org.wolflink.minecraft.plugin.siriuxa.task.tasks.infinite;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskStat;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.infinite.stage.InfiniteEndStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.infinite.stage.InfiniteGameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.infinite.stage.InfiniteReadyStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.infinite.stage.InfiniteWaitStage;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.StageHolder;

/**
 * 无尽任务
 * 所有玩家需要坚守到最后一刻
 * 根据游戏阶段，完成子任务数量，存活时间等结算任务奖励
 * 不存在任务失败的情况
 */
public class InfiniteTask extends Task {
    protected InfiniteTask(@NotNull GlobalTeam globalTeam, @NotNull TaskDifficulty taskDifficulty) {
        super(globalTeam, taskDifficulty);
    }
    InfiniteTaskStat infiniteTaskStat = new InfiniteTaskStat(this);
    @Override
    public TaskStat getTaskStat() {
        return infiniteTaskStat;
    }

    /**
     * 初始化任务快照
     */
    @Override
    public void initRecord() {
        //TODO 需要重构任务记录相关代码
    }

    /**
     * 填充任务快照
     *
     * @param offlinePlayer
     * @param taskResult
     */
    @Override
    public void fillRecord(OfflinePlayer offlinePlayer, boolean taskResult) {
        //TODO 需要重构任务记录相关代码
    }

    /**
     * 完成任务快照
     */
    @Override
    public void finishRecord() {
        //TODO 需要重构任务记录相关代码
    }

    @Override
    protected StageHolder initStageHolder() {
        TaskLinearStageHolder linearStageHolder = new TaskLinearStageHolder(this);
        linearStageHolder.bindStages(new Stage[]{new InfiniteWaitStage(linearStageHolder), new InfiniteReadyStage(linearStageHolder), new InfiniteGameStage(linearStageHolder), new InfiniteEndStage(linearStageHolder)});
        // 进入等待阶段
        linearStageHolder.next();
        return linearStageHolder;
    }

    @Override
    protected void failedCheck() {
        // 无需检查
    }

    /**
     * 任务队伍中没有玩家则结束无尽任务
     */
    @Override
    protected void finishedCheck() {
        subScheduler.runTaskTimer(() -> {
            if (getTaskTeamSize() == 0) {
                triggerFinish();
            }
        }, 20, 20);
    }

    @Override
    protected void implPreLoad() {

    }

    @Override
    public void start() {

    }

    @Override
    protected void finish() {}

    @Override
    public void failed() {

    }
}
