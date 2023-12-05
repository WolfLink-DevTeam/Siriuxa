package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components;

import org.jetbrains.annotations.Nullable;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.ComposableTask;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.environments.EnvironmentType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.goals.GoalType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.OrnamentType;

/**
 * 检查任务是否拥有可重用组件
 */
public class ComponentChecker {
    public static <T extends Task> boolean taskHasComponent(@Nullable T task, OrnamentType ornamentType) {
        if(task == null) return false;
        if(task instanceof ComposableTask composableTask) {
            return composableTask.getOrnamentTypes().contains(ornamentType);
        }
        return false;
    }
    public static <T extends Task> boolean taskHasComponent(@Nullable T task, GoalType goalType) {
        if(task == null) return false;
        if(task instanceof ComposableTask composableTask) {
            return composableTask.getGoalType().equals(goalType);
        }
        return false;
    }
    public static <T extends Task> boolean taskHasComponent(@Nullable T task, EnvironmentType environmentType) {
        if(task == null) return false;
        if(task instanceof ComposableTask composableTask) {
            return composableTask.getEnvironmentType().equals(environmentType);
        }
        return false;
    }
}
