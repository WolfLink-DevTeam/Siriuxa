package org.wolflink.minecraft.plugin.siriuxa.task.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.PlayerBackpack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 可以被序列化的任务记录
 */
@Data
@NoArgsConstructor
public class PlayerTaskRecord implements ConfigurationSerializable {
    private UUID playerUuid;
    private UUID taskUuid;

    private PlayerBackpack playerBackpack;
    /**
     * 任务用时
     */
    private long usingTimeInMills;
    /**
     * 任务完成时间
     */
    private long finishedTimeInMills;
    /**
     * 团队规模
     */
    private int teamSize;
    /**
     * 任务难度
     */
    private String taskDifficulty;
    /**
     * 任务类型
     */
    private String taskType;

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = new HashMap<>();
        map.put("playerUuid",playerUuid);
        map.put("taskUuid",taskUuid);
        map.put("playerBackpack",playerBackpack);
        map.put("usingTimeInMills",usingTimeInMills);
        map.put("finishedTimeInMills",finishedTimeInMills);
        map.put("teamSize",teamSize);
        map.put("taskDifficulty",taskDifficulty);
        map.put("taskType",taskType);
        return map;
    }
    public PlayerTaskRecord(Map<String,Object> map) {
        playerUuid = (UUID) map.get("playerUuid");
        taskUuid = (UUID) map.get("taskUuid");
        playerBackpack = (PlayerBackpack) map.get("playerBackpack");
        usingTimeInMills = (long) map.get("usingTimeInMills");
        finishedTimeInMills = (long) map.get("finishedTimeInMills");
        teamSize = (int) map.get("teamSize");
        taskDifficulty = (String) map.get("taskDifficulty");
        taskType = (String) map.get("taskType");
    }
    public static PlayerTaskRecord deserialize(Map<String,Object> map) {
        return new PlayerTaskRecord(map);
    }
}
