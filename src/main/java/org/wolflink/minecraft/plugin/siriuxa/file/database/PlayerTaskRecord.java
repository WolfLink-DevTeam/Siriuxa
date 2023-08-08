package org.wolflink.minecraft.plugin.siriuxa.file.database;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.wolflink.minecraft.plugin.siriuxa.backpack.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 可以被序列化的麦穗任务记录
 */
@Data
public class PlayerTaskRecord implements ConfigurationSerializable {
    private final UUID playerUuid;
    private final UUID taskUuid;
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
     * 奖励麦穗
     */
    private double rewardWheat;
    public PlayerTaskRecord(@NonNull UUID playerUuid, @NonNull Task task) {
        this.playerUuid = playerUuid;
        taskUuid = task.getTaskUuid();
        isSuccess = false;
        teamSize = task.getTaskTeamSize();
        taskType = task.getName();
        taskDifficulty = task.getTaskDifficulty().getName();
        isEscape = false;
        isClaimed = false;
        rewardWheat = 0;
    }

    public PlayerTaskRecord(Map<String, Object> map) {
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
        rewardWheat = (double) map.get("rewardWheat");
    }

    public static PlayerTaskRecord deserialize(Map<String, Object> map) {
        return new PlayerTaskRecord(map);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("playerUuid", playerUuid.toString());
        map.put("taskUuid", taskUuid.toString());
        map.put("isSuccess", isSuccess);
        map.put("playerBackpack", playerBackpack);
        map.put("usingTimeInMills", Long.toString(usingTimeInMills));
        map.put("finishedTimeInMills", Long.toString(finishedTimeInMills));
        map.put("teamSize", teamSize);
        map.put("taskDifficulty", taskDifficulty);
        map.put("taskType", taskType);
        map.put("isEscape", isEscape);
        map.put("isClaimed", isClaimed);
        map.put("rewardWheat", rewardWheat);
        return map;
    }
}
