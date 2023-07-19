package org.wolflink.minecraft.plugin.siriuxa.file.database;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 可以被序列化的任务记录
 */
@Data
public class PlayerTaskRecord implements ConfigurationSerializable {
    public PlayerTaskRecord(@NonNull UUID playerUuid,@NonNull Task task) {
        this.playerUuid = playerUuid;
        taskUuid = task.getTaskUuid();
        isSuccess = false;
        teamSize = task.size();
        taskType = task.getName();
        taskDifficulty = task.getTaskDifficulty().getName();
        isEscape = false;
        isClaimed = false;
    }
    private final UUID playerUuid;
    private final UUID taskUuid;

    /**
     * 任务奖励是否已被领取
     */
    private boolean isClaimed;

    /**
     * 玩家是否逃跑
     */
    private boolean isEscape;
    /**
     * 任务是否成功
     */
    private boolean isSuccess;
    /**
     * 玩家背包
     */
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
    private final int teamSize;
    /**
     * 任务难度
     */
    private final String taskDifficulty;
    /**
     * 任务类型
     */
    private final String taskType;

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = new HashMap<>();
        map.put("playerUuid",playerUuid.toString());
        map.put("taskUuid",taskUuid.toString());
        map.put("isSuccess",isSuccess);
        map.put("playerBackpack",playerBackpack);
        map.put("usingTimeInMills",Long.toString(usingTimeInMills));
        map.put("finishedTimeInMills",Long.toString(finishedTimeInMills));
        map.put("teamSize",teamSize);
        map.put("taskDifficulty",taskDifficulty);
        map.put("taskType",taskType);
        map.put("isEscape",isEscape);
        map.put("isClaimed",isClaimed);
        return map;
    }
    public PlayerTaskRecord(Map<String,Object> map) {
        playerUuid = UUID.fromString((String) map.get("playerUuid"));
        taskUuid = UUID.fromString((String) map.get("taskUuid"));
        isSuccess = (boolean) map.get("isSuccess");
        playerBackpack = (PlayerBackpack) map.get("playerBackpack");
        usingTimeInMills = Long.parseLong((String) map.get("usingTimeInMills"));
        finishedTimeInMills = Long.parseLong((String) map.get("finishedTimeInMills"));
        teamSize = (int) map.get("teamSize");
        taskDifficulty = (String) map.get("taskDifficulty");
        taskType = (String) map.get("taskType");
        isClaimed = (boolean) map.get("isClaimed");
        isEscape = (boolean) map.get("isEscape");
    }
    public static PlayerTaskRecord deserialize(Map<String,Object> map) {
        return new PlayerTaskRecord(map);
    }
}
