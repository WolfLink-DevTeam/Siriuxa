package priv.mikkoayaka.minecraft.plugin.seriuxajourney.file;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum ConfigProjection {
    DEBUG("Debug", false),
    LOBBY_WORLD_NAME("Lobby.WorldName", "kg"), // 任务大厅世界
    LOBBY_LOCATION("Lobby.Location", "-140 91 10"), // 任务大厅坐标
    EXPLORATION_TASK_WORLD_NAME("Task.Exploration.WorldName", "normal-exploration"),
    EXPLORATION_REGION_TOTAL_LENGTH("Task.Exploration.Region.TotalLength", 50000),
    EXPLORATION_REGION_INDEX("Task.Exploration.Region.Index", 1),
    EXPLORATION_REGION_SPACING_RADIUS("Task.Exploration.Region.SpacingRadius", 50),
    EXPLORATION_REGION_RADIUS("Task.Exploration.Region.Radius", 500.0);
    @Getter
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
