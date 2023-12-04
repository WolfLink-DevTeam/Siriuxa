package org.wolflink.minecraft.plugin.siriuxa.task.abstracts;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskProperties;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.ComposableTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TaskAttribute {

    protected Task task;
    public TaskAttribute(Task task) {
        this.task = task;
        init(task.getClass(),task.getTaskDifficulty().getLevel());
    }

    @Getter
    private final Map<TaskAttributeType,Object> attributeMap = new ConcurrentHashMap<>();

    public final void setAttribute(TaskAttributeType type,Object value) {
        attributeMap.put(type,value);
    }
    public final boolean hasAttribute(TaskAttributeType type) {
        return attributeMap.containsKey(type);
    }
    @Nullable
    public final <T> T getAttribute(TaskAttributeType type) {
        try {
            return (T) attributeMap.getOrDefault(type, null);
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.error("在尝试将 "+type.name()+" 的值进行类型转换时出现问题");
            return null;
        }
    }
    @NonNull
    public final <T> T getAttribute(TaskAttributeType type,T defaultValue) {
        T value = getAttribute(type);
        if(value == null) return defaultValue;
        return value;
    }

    /**
     * 初始化属性池
     */
    private void init(Class<? extends Task> taskClass,Integer difficultyLevel) {
        TaskProperties taskProperties = IOC.getBean(TaskProperties.class);
        attributeMap.clear();
        // 从模板克隆属性表
        try {
            attributeMap.putAll(taskProperties.getTaskProperties(taskClass,difficultyLevel));
        } catch (Exception ignore) {
            Notifier.error("暂不支持的难度等级："+difficultyLevel);
        }
    }
}
