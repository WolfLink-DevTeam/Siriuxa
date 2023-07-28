package org.wolflink.minecraft.plugin.siriuxa.file.database;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;

import javax.annotation.Nullable;
import java.io.File;

@Singleton
public class OfflinePlayerDB extends FileDB {
    public OfflinePlayerDB() {
        super("offline_players");
    }

    /**
     * 在玩家即将离线时触发
     */
    public void save(OfflinePlayerRecord offlinePlayerRecord) {
        File dataFile = new File(folder, offlinePlayerRecord.getName() + ".yml");
        FileConfiguration fileConfiguration = getFileConfiguration(dataFile);
        if (fileConfiguration == null) fileConfiguration = createAndLoad(dataFile);
        fileConfiguration.set("data", offlinePlayerRecord);
        try {
            fileConfiguration.save(dataFile);
            Notifier.debug("玩家" + dataFile.getName() + "的离线数据已保存。");
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.error("在尝试保存离线玩家记录" + dataFile.getName() + "的时候出现了问题。");
        }
    }

    @Nullable
    public OfflinePlayerRecord load(OfflinePlayer offlinePlayer) {
        File dataFile = new File(folder, offlinePlayer.getName() + ".yml");
        FileConfiguration fileConfiguration = getFileConfiguration(dataFile);
        if (fileConfiguration == null) return null;
        try {
            return (OfflinePlayerRecord) fileConfiguration.get("data");
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.error("在尝试读取离线玩家记录" + dataFile.getName() + "的时候出现了问题。");
        }
        return null;
    }
}
