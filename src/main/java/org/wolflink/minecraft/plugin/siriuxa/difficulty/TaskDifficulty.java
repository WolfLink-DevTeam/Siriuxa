package org.wolflink.minecraft.plugin.siriuxa.difficulty;

import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.wolflink.minecraft.plugin.siriuxa.api.INameable;

import java.util.HashMap;
import java.util.Map;

@Data
public class TaskDifficulty implements ConfigurationSerializable,INameable{
    /**
     * 难度图标
     */
    protected final Material icon;
    /**
     * 难度颜色
     */
    protected final String color;
    /**
     * 难度等级
     */
    protected final int level;
    /**
     * 难度名称
     */
    protected final String name;
    @Builder
    public TaskDifficulty(Material icon, String color, int level, String name) {
        this.icon = icon;
        this.color = color;
        this.level = level;
        this.name = name;
    }
    protected TaskDifficulty(Map<String,Object> map) {
        this.icon = (Material) map.get("icon");
        this.color = (String) map.get("color");
        this.level = (int) map.get("level");
        this.name = (String) map.get("name");
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = new HashMap<>();
        map.put("icon",icon);
        map.put("color",color);
        map.put("level",level);
        map.put("name",name);
        return map;
    }
    public static TaskDifficulty deserialize(Map<String,Object> map) {
        return new TaskDifficulty(map);
    }
}
