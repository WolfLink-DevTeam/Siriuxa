package org.wolflink.minecraft.plugin.siriuxa.file.database;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.DateAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.backpack.EnderBackpack;
import org.wolflink.minecraft.plugin.siriuxa.backpack.PlayerBackpack;

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
    private final File fiveSlotDataFolder = new File(folder, "five_slot");
    @Inject
    private DateAPI dateAPI;

    public InventoryDB() {
        super("inventory");
        if (!mainDataFolder.exists()) mainDataFolder.mkdirs();
        if (!cacheDataFolder.exists()) cacheDataFolder.mkdirs();
        if (!fiveSlotDataFolder.exists()) fiveSlotDataFolder.mkdirs();
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

    /**
     * 会覆盖原来的5格背包信息
     */
    public void saveEnderBackpack(OfflinePlayer offlinePlayer, EnderBackpack enderBackpack) {
        File enderBackpackFile = new File(fiveSlotDataFolder,offlinePlayer.getName()+".yml");
        if(enderBackpackFile.exists()) delete(enderBackpackFile);
        FileConfiguration fileConfiguration = createAndLoad(enderBackpackFile);
        fileConfiguration.set("data", enderBackpack);
        save(enderBackpackFile);
    }

    /**
     * 如果数据库中不存在则传回空的5格背包信息
     * 存在则传回玩家自定义的5格背包信息
     */
    public EnderBackpack loadEnderBackpack(OfflinePlayer offlinePlayer) {
        File enderBackpackFile = new File(fiveSlotDataFolder,offlinePlayer.getName()+".yml");
        if(!enderBackpackFile.exists()) return new EnderBackpack();
        FileConfiguration fileConfiguration = getFileConfiguration(enderBackpackFile);
        if(fileConfiguration == null) {
            Notifier.error("存在玩家 "+offlinePlayer.getName()+" 的背包数据文件，但无法读取其 FileConfiguration 对象");
            return new EnderBackpack();
        }
        return (EnderBackpack) fileConfiguration.get("data");
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
        save(mainInvFile);
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
            save(cacheFile);
        });
    }
}
