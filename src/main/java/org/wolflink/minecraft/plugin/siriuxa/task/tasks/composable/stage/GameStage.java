package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.stage;

import lombok.NonNull;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.BaseGameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;

import java.util.function.Supplier;

public class GameStage extends BaseGameStage {
    public GameStage(TaskLinearStageHolder stageHolder) {
        super(stageHolder);
    }

    @Override
    protected @NonNull Supplier<Boolean> nextCondition() {
        return ()-> getStageHolder().getTask().getTaskLifeCycle().isTaskOver();
    }

    @Override
    protected @NonNull Long nextCheckPeriodTick() {
        return 20L * 5;
    }

    @Override
    protected void initScheduleTask() {
        Task task = getStageHolder().getTask();
        getSubScheduler().runTaskLater(task.getTaskLifeCycle()::start,20L * 3);
    }

    @Override
    protected void onEnter() {
        Task task = getStageHolder().getTask();
        for (Player player : task.getGlobalTeam().getPlayers()) {
            player.sendTitle("§a§l任务开始", "§7作业单元已准备就绪，开始投放", 10, 40, 10);
            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 0.5f);
            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1f, 0.5f);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 0.5f);
        }
    }

    @Override
    protected void onLeave() {

    }
}
