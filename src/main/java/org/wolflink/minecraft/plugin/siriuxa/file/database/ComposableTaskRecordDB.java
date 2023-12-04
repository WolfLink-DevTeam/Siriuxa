package org.wolflink.minecraft.plugin.siriuxa.file.database;

import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.impl.ComposableTaskRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ComposableTaskRecordDB extends FileDB {
    public ComposableTaskRecordDB() {
        super("composable_task_cache");
    }

    /**
     * 将 ComposableTaskRecord 保存为任务记录文件
     */
    public void saveRecord(ComposableTaskRecord composableTaskRecord) {
        // 用任务 UUID 作为文件名
        File recordFile = new File(folder, composableTaskRecord.getTaskUuid().toString() + ".yml");
        FileConfiguration configuration = createAndLoad(recordFile);
        configuration.set("data", composableTaskRecord);
        try {
            configuration.save(recordFile);
            Notifier.debug("任务 " + composableTaskRecord.getTaskUuid() + " 记录已保存。");
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.error("在尝试保存任务记录时出现异常。");
        }
    }

    /**
     * 根据任务UUID读取相应的任务记录文件
     *
     * 如果不存在该UUID对应的任务记录，则会传回 null
     */
    @Nullable
    public ComposableTaskRecord loadRecord(String taskUuidString) {
        File recordFile = new File(folder,taskUuidString+".yml");
        FileConfiguration configuration = getFileConfiguration(recordFile);
        if(configuration == null) {
            return null;
        }
        try {
            ComposableTaskRecord record = (ComposableTaskRecord) configuration.get("data");
            return record;
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.error("在尝试转换任务记录文件："+ recordFile.getAbsolutePath() +"为 ComposableTaskRecord 类型时出现问题。");
        }
        return null;
    }

    @NonNull
    public List<PlayerTaskRecord> loadRecords(String playerName) {
        List<PlayerTaskRecord> result = new ArrayList<>();
        File playerFolder = new File(folder, playerName);
        if (!playerFolder.exists()) return result;
        File[] recordFiles = playerFolder.listFiles();
        if (recordFiles == null) return result;

        for (File recordFile : recordFiles) {
            FileConfiguration fileConfiguration = getFileConfiguration(recordFile);
            if (fileConfiguration == null) {
                Notifier.error("任务记录文件：" + recordFile.getAbsolutePath() + "的FileConfiguration对象不存在。");
                continue;
            }
            try {
                PlayerTaskRecord playerTaskRecord = (PlayerTaskRecord) fileConfiguration.get("data");
                if (playerTaskRecord != null) result.add(playerTaskRecord);
            } catch (Exception e) {
                e.printStackTrace();
                Notifier.error("在尝试转换任务记录文件：" + recordFile.getAbsolutePath() + "为 PlayerTaskRecord 类型时出现问题。");
            }
        }
        return result;
    }
}
