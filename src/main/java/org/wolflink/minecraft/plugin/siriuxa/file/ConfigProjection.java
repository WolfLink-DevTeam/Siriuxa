package org.wolflink.minecraft.plugin.siriuxa.file;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ConfigProjection {
    DEBUG("Debug", false),
    LOBBY_WORLD_NAME("Lobby.WorldName", "kg"), // 任务大厅世界
    LOBBY_LOCATION("Lobby.Location", "-140 91 10"), // 任务大厅坐标
    LOBBY_READY_LOCATION("Lobby.ReadyLocation", "-185 93 10"), // 任务准备区域的中心坐标
    LOBBY_READY_RADIUS("Lobby.ReadyRadius", 5), // 任务准备区域的半径
    EXPLORATION_TASK_WORLD_NAME("Task.Exploration.WorldName", "normal-exploration"),
    EXPLORATION_REGION_TOTAL_LENGTH("Task.Exploration.Region.TotalLength", 50000),
    EXPLORATION_REGION_INDEX("Task.Exploration.Region.Index", 1),
    EXPLORATION_REGION_SPACING_RADIUS("Task.Exploration.Region.SpacingRadius", 50),
    EXPLORATION_REGION_RADIUS("Task.Exploration.Region.Radius", 500.0),
    EXPLORATION_TASK_QUEUE_SIZE("Task.Exploration.QueueSize", 8);
    private final String path;
    @Getter
    private final Object defaultValue;

    ConfigProjection(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public static Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        for (ConfigProjection configProjection : ConfigProjection.values()) {
            map.put(configProjection.path, configProjection.defaultValue);
        }
        return map;
    }
}
