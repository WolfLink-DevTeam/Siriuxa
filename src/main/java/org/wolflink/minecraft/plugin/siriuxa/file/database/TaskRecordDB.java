package org.wolflink.minecraft.plugin.siriuxa.file.database;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.DateAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.SerializeAPI;
import org.wolflink.minecraft.plugin.siriuxa.task.common.PlayerTaskRecord;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import java.io.File;

@Singleton
public class TaskRecordDB extends FileDB{
    public TaskRecordDB() {
        super("task_cache");
    }
    @Inject
    private DateAPI dateAPI;
    @Inject
    private SerializeAPI serializeAPI;

    public void saveRecord(PlayerTaskRecord playerTaskRecord) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerTaskRecord.getPlayerUuid());
        String playerName = offlinePlayer.getName();
        if(playerName == null) {
            Notifier.error("在尝试保存数据记录时，离线玩家名为空！");
            return;
        }
        File playerFolder = new File(folder,playerName);
        if(!playerFolder.exists())playerFolder.mkdirs();
        // 用任务 UUID 作为文件名
        File recordFile = new File(playerFolder,playerTaskRecord.getTaskUuid().toString());
        FileConfiguration configuration = createAndLoad(recordFile);
//        configuration.set("data",);

    }
}
