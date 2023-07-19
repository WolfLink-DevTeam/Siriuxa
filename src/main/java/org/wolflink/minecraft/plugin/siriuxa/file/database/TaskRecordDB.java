package org.wolflink.minecraft.plugin.siriuxa.file.database;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class TaskRecordDB extends FileDB {
    public TaskRecordDB() {
        super("task_cache");
    }

    public void saveRecord(PlayerTaskRecord playerTaskRecord) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerTaskRecord.getPlayerUuid());
        String playerName = offlinePlayer.getName();
        if (playerName == null) {
            Notifier.error("在尝试保存数据记录时，离线玩家名为空！");
            return;
        }
        File playerFolder = new File(folder, playerName);
        if (!playerFolder.exists()) playerFolder.mkdirs();
        // 用任务 UUID 作为文件名
        File recordFile = new File(playerFolder, playerTaskRecord.getTaskUuid().toString() + ".yml");
        FileConfiguration configuration = createAndLoad(recordFile);
        configuration.set("data", playerTaskRecord);
        try {
            configuration.save(recordFile);
            Notifier.debug(offlinePlayer.getName() + "的任务记录" + playerTaskRecord.getTaskUuid() + "已保存。");
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.error("在尝试保存任务记录时出现异常。");
        }
    }
    @NonNull
    public List<PlayerTaskRecord> loadRecords(String playerName) {
        List<PlayerTaskRecord> result = new ArrayList<>();
        File playerFolder = new File(folder, playerName);
        if (!playerFolder.exists()) return result;
        File[] recordFiles = playerFolder.listFiles();
        if(recordFiles == null) return result;

        for (File recordFile : recordFiles) {
            FileConfiguration fileConfiguration = getFileConfiguration(recordFile);
            if(fileConfiguration == null) {
                Notifier.error("任务记录文件："+recordFile.getAbsolutePath()+"的FileConfiguration对象不存在。");
                continue;
            }
            try {
                PlayerTaskRecord playerTaskRecord = (PlayerTaskRecord) fileConfiguration.get("data");
                if(playerTaskRecord != null) result.add(playerTaskRecord);
            } catch (Exception e) {
                e.printStackTrace();
                Notifier.error("在尝试转换任务记录文件："+recordFile.getAbsolutePath()+"为 PlayerTaskRecord 类型时出现问题。");
            }
        }
        return result;
    }
}
