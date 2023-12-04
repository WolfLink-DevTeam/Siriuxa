package org.wolflink.minecraft.plugin.siriuxa.task.tasks.common;

import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskAttributeType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.ComposableTask;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务默认属性表(静态模板)
 */
@Singleton
public class TaskProperties {
    private final Map<Class<? extends Task>,Map<Integer,Map<TaskAttributeType,Object>>> properties = new HashMap<>();
    public Map<TaskAttributeType,Object> getTaskProperties(Class<? extends Task> taskClass,Integer difficultyLevel) {
        return properties.get(taskClass).get(difficultyLevel);
    }
    public TaskProperties() {
        // ComposableTask
        {
            Map<Integer,Map<TaskAttributeType,Object>> composableTaskProperties = new HashMap<>();
            // 难度 1
            {
                Map<TaskAttributeType,Object> attributeMap = new HashMap<>();
                attributeMap.put(TaskAttributeType.WHEAT_COST,30);
                attributeMap.put(TaskAttributeType.LUMEN_SUPPLY,200);
                attributeMap.put(TaskAttributeType.BASE_LUMEN_LOSS,0.1);
                attributeMap.put(TaskAttributeType.LUMEN_LOST_ACC_SPEED,0.0008);
                attributeMap.put(TaskAttributeType.HURT_LUMEN_COST,1.5);
                attributeMap.put(TaskAttributeType.HURT_DAMAGE_MULTIPLE,0.5);
                attributeMap.put(TaskAttributeType.REWARD_MULTIPLE,0.75);
                attributeMap.put(TaskAttributeType.BRING_SLOT_AMOUNT,8);
                composableTaskProperties.put(1,attributeMap);
            }
            // 难度 2
            {
                Map<TaskAttributeType,Object> attributeMap = new HashMap<>();
                attributeMap.put(TaskAttributeType.WHEAT_COST,60);
                attributeMap.put(TaskAttributeType.LUMEN_SUPPLY,200);
                attributeMap.put(TaskAttributeType.BASE_LUMEN_LOSS,0.00093);
                attributeMap.put(TaskAttributeType.HURT_LUMEN_COST,2.0);
                attributeMap.put(TaskAttributeType.HURT_DAMAGE_MULTIPLE,1.0);
                attributeMap.put(TaskAttributeType.REWARD_MULTIPLE,1.0);
                attributeMap.put(TaskAttributeType.BRING_SLOT_AMOUNT,10);
                composableTaskProperties.put(2,attributeMap);
            }
            // 难度 3
            {
                Map<TaskAttributeType,Object> attributeMap = new HashMap<>();
                attributeMap.put(TaskAttributeType.WHEAT_COST,90);
                attributeMap.put(TaskAttributeType.LUMEN_SUPPLY,200);
                attributeMap.put(TaskAttributeType.BASE_LUMEN_LOSS,0.001);
                attributeMap.put(TaskAttributeType.HURT_LUMEN_COST,2.4);
                attributeMap.put(TaskAttributeType.HURT_DAMAGE_MULTIPLE,1.2);
                attributeMap.put(TaskAttributeType.REWARD_MULTIPLE,1.5);
                attributeMap.put(TaskAttributeType.BRING_SLOT_AMOUNT,12);
                composableTaskProperties.put(3,attributeMap);
            }
            // 难度 4
            {
                Map<TaskAttributeType,Object> attributeMap = new HashMap<>();
                attributeMap.put(TaskAttributeType.WHEAT_COST,120);
                attributeMap.put(TaskAttributeType.LUMEN_SUPPLY,200);
                attributeMap.put(TaskAttributeType.BASE_LUMEN_LOSS,0.0012);
                attributeMap.put(TaskAttributeType.HURT_LUMEN_COST,2.7);
                attributeMap.put(TaskAttributeType.HURT_DAMAGE_MULTIPLE,1.5);
                attributeMap.put(TaskAttributeType.REWARD_MULTIPLE,2.0);
                attributeMap.put(TaskAttributeType.BRING_SLOT_AMOUNT,12);
                composableTaskProperties.put(4,attributeMap);
            }
            properties.put(ComposableTask.class,composableTaskProperties);
        }
    }
}
