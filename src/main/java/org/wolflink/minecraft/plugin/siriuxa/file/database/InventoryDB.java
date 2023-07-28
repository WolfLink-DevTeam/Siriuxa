package org.wolflink.minecraft.plugin.siriuxa.file.database;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.DateAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.PlayerBackpack;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Calendar;

/**
 * 记录背包数据
 */
@Singleton
public class InventoryDB extends FileDB {
    private final File mainDataFolder = new File(folder, "main");
    private final File cacheDataFolder = new File(folder, "cache");
    @Inject
    private DateAPI dateAPI;

    public InventoryDB() {
        super("inventory");
        if (!mainDataFolder.exists()) mainDataFolder.mkdirs();
        if (!cacheDataFolder.exists()) cacheDataFolder.mkdirs();
    }

    @Nullable
    public PlayerBackpack loadMain(Player player) {
        File mainInvFile = new File(mainDataFolder, player.getName() + ".yml");
        FileConfiguration fileConfiguration = getFileConfiguration(mainInvFile);
        if (fileConfiguration == null) {
            Notifier.debug("未能获取到玩家的主要背包信息");
            return null;
        }
        PlayerBackpack playerBackpack = (PlayerBackpack) fileConfiguration.get("data");
        Notifier.debug("已加载玩家的主要背包信息");
        return playerBackpack;
    }

    public void saveMain(Player player, PlayerBackpack playerBackpack) {
        File mainInvFile = new File(mainDataFolder, player.getName() + ".yml");
        FileConfiguration fileConfiguration = getFileConfiguration(mainInvFile);
        if (fileConfiguration != null) {
            PlayerBackpack oldPack = (PlayerBackpack) fileConfiguration.get("data");
            saveCache(player, oldPack);
        }
        fileConfiguration = createAndLoad(mainInvFile);
        fileConfiguration.set("data", playerBackpack);
        Notifier.debug("已保存玩家" + player.getName() + "的主要背包信息。");
    }

    private void saveCache(Player player, PlayerBackpack playerBackpack) {
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {
            File cacheInvFolder = new File(cacheDataFolder, player.getName());
            if (!cacheInvFolder.exists()) cacheInvFolder.mkdirs();
            String time = dateAPI.getTime(Calendar.getInstance());
            File cacheFile = new File(cacheInvFolder, time + ".yml");
            FileConfiguration cache = createAndLoad(cacheFile);
            cache.set("data", playerBackpack);
            try {
                cache.save(cacheFile);
                Notifier.debug("已保存玩家" + player.getName() + "在时间" + time + "的缓存背包信息。");
            } catch (Exception e) {
                e.printStackTrace();
                Notifier.error("在尝试保存玩家" + player.getName() + "背包缓存信息时出现问题。");
            }
        });
    }
}
