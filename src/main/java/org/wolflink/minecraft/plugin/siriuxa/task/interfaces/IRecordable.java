package org.wolflink.minecraft.plugin.siriuxa.task.interfaces;

import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerTaskRecord;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskStat;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface IRecordable {

    Map<UUID, PlayerTaskRecord> getPlayerRecordMap();

    TaskStat getTaskStat();

    @Nullable
    default PlayerTaskRecord getPlayerTaskRecord(UUID uuid) {
        return getPlayerRecordMap().get(uuid);
    }

    @NonNull
    default Collection<PlayerTaskRecord> getPlayerTaskRecords() {
        return getPlayerRecordMap().values();
    }

    /**
     * 初始化任务快照
     */
    void initRecord();

    /**
     * 填充任务快照
     */
    void fillRecord(OfflinePlayer offlinePlayer, boolean taskResult);

    /**
     * 完成任务快照
     */
    void finishRecord();
}
