package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.impl;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.IStatus;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.environments.EnvironmentType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.goals.GoalType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.OrnamentType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 组合任务滚动器
 *
 * 每过2小时刷新一次任务的 类型/目标/环境
 */
@Getter
@Singleton
public class ComposableTaskScroller implements IStatus {

    private Set<OrnamentType> ornamentTypes = new HashSet<>();
    private EnvironmentType environmentType = EnvironmentType.NORMAL_WORLD;
    private GoalType goalType = GoalType.COLLECTION;
    private int taskId = -1;
    private void refresh() {
        int ornamentCount = ThreadLocalRandom.current().nextInt(3,6);
        ornamentTypes = Arrays.stream(OrnamentType.values()).limit(ornamentCount).collect(Collectors.toSet());
        environmentType = EnvironmentType.values()[(int) (Math.random() * EnvironmentType.values().length)];
        goalType = GoalType.values()[(int) (Math.random() * GoalType.values().length)];
    }

    @Override
    public void enable() {
        taskId = Bukkit.getScheduler().runTaskTimer(Siriuxa.getInstance(),
                this::refresh,20 * 60 * 120,20 * 60 * 120).getTaskId();
    }

    @Override
    public void disable() {
        Bukkit.getScheduler().cancelTask(taskId);
        taskId = -1;
    }
}
