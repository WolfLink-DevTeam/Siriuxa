package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.impl;

import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.StringAPI;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskAttributeType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.ComposableTask;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.environments.EnvironmentType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.goals.GoalType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.OrnamentType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 可组合类型任务记录类
 */
@Data
public class ComposableTaskRecord implements ConfigurationSerializable {
    private UUID taskUuid;
    /**
     * 团队规模
     */
    private int teamSize;
    /**
     * 任务难度
     */
    private String taskDifficulty;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务是否成功
     */
    private boolean isSuccess;
    /**
     * 任务完成时间
     */
    private long finishedTimeInMills;
    /**
     * 任务属性记录
     */
    private Map<TaskAttributeType,Object> attributeMap;
    /**
     * 任务修饰
     */
    private Set<OrnamentType> ornamentTypes;
    /**
     * 环境类型
     */
    private EnvironmentType environmentType;
    /**
     * 任务目标
     */
    private GoalType goalType;
    public ComposableTaskRecord(){

    }
    public ComposableTaskRecord(Map<String,Object> map) {
        taskUuid = UUID.fromString((String) map.get("taskUuid"));
        isSuccess = (boolean) map.get("isSuccess");
        finishedTimeInMills = (long) map.get("finishedTimeInMills");
        teamSize = (int) map.get("teamSize");
        taskDifficulty = (String) map.get("taskDifficulty");
        taskName = (String) map.get("taskName");
        ornamentTypes = IOC.getBean(StringAPI.class)
                .spliting((String) map.get("ornamentTypes"),OrnamentType::valueOf,' ')
                .collect(Collectors.toSet());
        environmentType = EnvironmentType.valueOf((String) map.get("environmentType"));
        goalType = GoalType.valueOf((String) map.get("goalType"));
        attributeMap = new HashMap<>();
        // TODO 解析 AttributeMap
    }
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String,Object> map = new HashMap<>();
        map.put("taskUuid", taskUuid.toString());
        map.put("isSuccess", isSuccess);
        map.put("finishedTimeInMills", finishedTimeInMills);
        map.put("teamSize", teamSize);
        map.put("taskDifficulty", taskDifficulty);
        map.put("taskName", taskName);
        String ornamentTypesString = IOC.getBean(StringAPI.class).joining(ornamentTypes,OrnamentType::name,' ');
        map.put("ornamentTypes", ornamentTypesString);
        map.put("environmentType",environmentType.name());
        map.put("goalType",goalType.name());
        attributeMap.forEach((k,v) -> {
            map.put("-"+k.name(),v);
        });
        return map;
    }
    public static ComposableTaskRecord deserialize(Map<String, Object> map) {
        return new ComposableTaskRecord(map);
    }
    public static ComposableTaskRecord from(ComposableTask task) {
        ComposableTaskRecord record = new ComposableTaskRecord();
        record.taskUuid = task.getTaskUuid();
        record.teamSize = task.getTaskTeamSize();
        record.taskDifficulty = task.getTaskDifficulty().getName();
        record.taskName = task.getName();
        record.isSuccess = task.getTaskLifeCycle().isFinished();
        record.finishedTimeInMills = 0;
        record.ornamentTypes = task.getOrnamentTypes();
        record.environmentType = task.getEnvironmentType();
        record.goalType = task.getGoalType();
        record.attributeMap = task.getTaskAttribute().getAttributeMap();
        return record;
    }
}
