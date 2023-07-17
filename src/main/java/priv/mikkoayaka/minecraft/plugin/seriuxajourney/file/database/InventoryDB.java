package priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.DateAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.invbackup.PlayerBackpack;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Calendar;

/**
 * 记录背包数据
 */
@Singleton
public class InventoryDB extends FileDB{
    @Inject
    private DateAPI dateAPI;
    private final File mainDataFolder = new File(folder,"main");
    private final File cacheDataFolder = new File(folder,"cache");
    public InventoryDB() {
        super("inventory");
        if(!mainDataFolder.exists()) mainDataFolder.mkdirs();
        if(!cacheDataFolder.exists()) cacheDataFolder.mkdirs();
    }
    @Nullable
    public PlayerBackpack loadMain(Player player) {
        File mainInvFile = new File(mainDataFolder,player.getName()+".yml");
        FileConfiguration fileConfiguration = getFileConfiguration(mainInvFile);
        if(fileConfiguration == null) {
            Notifier.debug("未能获取到玩家的主要背包信息");
            return null;
        }
        JsonObject jo = new Gson().fromJson(fileConfiguration.getString("data"), JsonObject.class);
        Notifier.debug("已加载玩家的主要背包信息");
        return PlayerBackpack.fromJsonObject(jo);
    }
    public void saveMain(Player player, PlayerBackpack playerBackpack) {
        File mainInvFile = new File(mainDataFolder,player.getName()+".yml");
        FileConfiguration fileConfiguration = getFileConfiguration(mainInvFile);
        if (fileConfiguration != null) {
            JsonObject oldJO = new Gson().fromJson(fileConfiguration.getString("data"),JsonObject.class);
            PlayerBackpack oldPack = PlayerBackpack.fromJsonObject(oldJO);
            saveCache(player,oldPack);
        } else {
            fileConfiguration = createAndLoad(mainInvFile);
            fileConfiguration.set("data",playerBackpack.toJsonObject().toString());
            Notifier.debug("已保存玩家"+player.getName()+"的主要背包信息。");
        }
    }
    private void saveCache(Player player,PlayerBackpack playerBackpack) {
        File cacheInvFolder = new File(cacheDataFolder,player.getName());
        if(!cacheInvFolder.exists()) cacheInvFolder.mkdirs();
        String time = dateAPI.getTime(Calendar.getInstance());
        FileConfiguration cache = createAndLoad(new File(cacheInvFolder,time+".yml"));
        cache.set("data",playerBackpack.toJsonObject().toString());
        Notifier.debug("已保存玩家"+player.getName()+"在时间"+time+"的缓存背包信息。");
    }
}