package org.wolflink.minecraft.plugin.siriuxa.task.abstracts;

import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerTaskRecord;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskStat;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TaskRecorder {

    protected final Map<UUID, PlayerTaskRecord> playerRecordMap = new ConcurrentHashMap<>();
    protected final Task task;

    public TaskRecorder(Task task) {
        this.task = task;
    }

    @Nullable
    public PlayerTaskRecord getPlayerTaskRecord(UUID uuid) {
        return playerRecordMap.get(uuid);
    }

    @NonNull
    public Collection<PlayerTaskRecord> getPlayerTaskRecords() {
        return playerRecordMap.values();
    }

    /**
     * 初始化任务快照
     */
    public abstract void initRecord();

    /**
     * 填充任务快照
     */
    public abstract void fillRecord(OfflinePlayer offlinePlayer, boolean taskResult);

    /**
     * 完成任务快照
     */
    public abstract void finishRecord();
}
