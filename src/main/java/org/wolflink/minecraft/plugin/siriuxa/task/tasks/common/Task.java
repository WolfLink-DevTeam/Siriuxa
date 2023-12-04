package org.wolflink.minecraft.plugin.siriuxa.task.tasks.common;

import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.INameable;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.monster.StrategyDecider;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.*;
import org.wolflink.minecraft.plugin.siriuxa.task.interfaces.IGlobalTeam;
import org.wolflink.minecraft.plugin.siriuxa.task.interfaces.ITaskTeam;
import org.wolflink.minecraft.plugin.siriuxa.task.regions.TaskArea;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeam;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.UUID;

/**
 * 抽象任务类
 */
@Data
public abstract class Task implements IGlobalTeam, ITaskTeam, INameable {

    /**
     * 任务基本属性
     */
    protected final UUID taskUuid = UUID.randomUUID();
    protected final String taskName;
    protected final String taskColor;

    /**
     * 任务基本组成部分
     */
    protected TaskLifeCycle taskLifeCycle; //任务生命周期
    protected TaskRecorder taskRecorder; //任务记录
    protected TaskStat taskStat; //任务状态统计
    protected TaskAttribute taskAttribute; //任务属性
    protected TaskListener taskListener; //任务监听器
    protected TaskRegion taskRegion; //任务区域
    protected TaskLinearStageHolder stageHolder; //任务阶段管理
    protected final StrategyDecider strategyDecider; //刷怪决策

    /**
     * 任务依赖对象
     */
    protected final SubScheduler subScheduler = new SubScheduler();
    protected final TaskDifficulty taskDifficulty;
    @NonNull protected GlobalTeam globalTeam;
    @Nullable protected TaskTeam taskTeam = null;

    protected Task(
            @NotNull GlobalTeam globalTeam,
            @NotNull TaskDifficulty taskDifficulty,
            @NotNull String taskName,
            @NotNull String taskColor
    ) {
        this.globalTeam = globalTeam;
        this.taskDifficulty = taskDifficulty;
        this.taskName = taskName;
        this.taskColor = taskColor;
        strategyDecider = new StrategyDecider(this);
    }

    @Override
    public String getName() {
        return taskName;
    }

    @Override
    public String getColor() {
        return taskColor;
    }

    protected void init(
            @NotNull TaskLifeCycle taskLifeCycle,
            @NotNull TaskRecorder taskRecorder,
            @NotNull TaskStat taskStat,
            @NotNull TaskAttribute taskAttribute,
            @NotNull TaskListener taskListener,
            @NotNull TaskRegion taskRegion,
            @NotNull TaskLinearStageHolder stageHolder
    ) {
        this.taskLifeCycle = taskLifeCycle;
        this.taskRecorder = taskRecorder;
        this.taskStat = taskStat;
        this.taskAttribute = taskAttribute;
        this.taskListener = taskListener;
        this.taskRegion = taskRegion;
        this.stageHolder = stageHolder;
    }

    /**
     * 停止所有检查
     */
    public void stopCheck() {
        strategyDecider.disable();
        subScheduler.cancelAllTasks();
        if (taskRegion != null && taskRegion.getTaskArea() != null) {
            taskRegion.getTaskArea().stopCheck();
            taskRegion.setTaskArea(null);
        }
    }

    /**
     * 清理本次任务
     * 在任务完成/失败后调用
     */
    public void deleteTask() {
        globalTeam.setSelectedTask(null);
        if (taskTeam != null) taskTeam.clear();
        IOC.getBean(TaskRepository.class).deleteByKey(taskUuid);
        // 释放队列
        IOC.getBean(TaskQueue.class).taskEnded();
        globalTeam = new GlobalTeam(null);
    }
}
