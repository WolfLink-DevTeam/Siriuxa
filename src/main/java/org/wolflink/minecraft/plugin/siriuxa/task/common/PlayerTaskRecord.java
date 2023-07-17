package org.wolflink.minecraft.plugin.siriuxa.task.common;

import lombok.Builder;
import lombok.Data;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.PlayerBackpack;

import java.util.UUID;

/**
 * 可以被序列化的任务记录
 */
@Data
@Builder
public class PlayerTaskRecord {
    private final UUID playerUuid;
    private final UUID taskUuid;

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
}
