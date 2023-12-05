package org.wolflink.minecraft.plugin.siriuxa.file.database;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.wolflink.minecraft.plugin.siriuxa.backpack.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.OrnamentType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 可以被序列化的通用玩家任务记录
 */
@Data
public class PlayerTaskRecord implements ConfigurationSerializable {
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
     * 该玩家是否完成任务
     * (如果玩家在任务过程中淘汰，即使任务成功，玩家也不能够领取到全部奖励)
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
     * 记录时间
     */
    private long createTime;
    /**
     * 玩家得分
     */
    private double playerScore;

    public PlayerTaskRecord(@NonNull UUID playerUuid, @NonNull Task task) {
        this.playerUuid = playerUuid;
        taskUuid = task.getTaskUuid();
        isEscape = false;
        isClaimed = false;
        playerScore = 0;
        createTime = Calendar.getInstance().getTimeInMillis();
    }

    public PlayerTaskRecord(Map<String, Object> map) {
        playerUuid = UUID.fromString((String) map.get("playerUuid"));
        taskUuid = UUID.fromString((String) map.get("taskUuid"));
        playerBackpack = (PlayerBackpack) map.get("playerBackpack");
        usingTimeInMills = Long.parseLong((String) map.get("usingTimeInMills"));
        isClaimed = (boolean) map.get("isClaimed");
        isEscape = (boolean) map.get("isEscape");
        playerScore = (double) map.get("playerScore");
        createTime = (long) map.get("createTime");
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
        map.put("playerBackpack", playerBackpack);
        map.put("usingTimeInMills", Long.toString(usingTimeInMills));
        map.put("isEscape", isEscape);
        map.put("isClaimed", isClaimed);
        map.put("playerScore", playerScore);
        map.put("createTime", createTime);
        return map;
    }
}
