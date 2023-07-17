package priv.mikkoayaka.minecraft.plugin.seriuxajourney.file;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.config.YamlConfig;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.DateAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.invbackup.PlayerBackpack;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class InventoryCache extends YamlConfig {
    @Inject
    private DateAPI dateAPI;
    public InventoryCache() {
        super("SeriuxaJourneyInventoryCache", new HashMap<>());
    }
    private String getMainInvPath(Player player) {
        return "MainInv."+player.getName();
    }
    private String getCacheMainInvPath(Player player) {
        return "Cache."+dateAPI.getDate(Calendar.getInstance())+"."+getMainInvPath(player);
    }
    public void saveMainInv(Player player, PlayerBackpack playerBackpack) {
        String oldStr = get(getMainInvPath(player));
        if (oldStr != null && !oldStr.isEmpty()) {
            JsonObject oldJO = new Gson().fromJson(oldStr,JsonObject.class);
            update(getCacheMainInvPath(player),oldJO.toString());
        }
        update(getMainInvPath(player),playerBackpack.toJsonObject().toString());
    }
    @Nullable
    public PlayerBackpack getMainInv(Player player) {
        String jsonStr = get(getMainInvPath(player));
        if(jsonStr == null || jsonStr.isEmpty()) {
            Notifier.debug("未获取到玩家背包数据"+player.getName());
            return null;
        }
        JsonObject jo = new Gson().fromJson(jsonStr, JsonObject.class);
        return PlayerBackpack.fromJsonObject(jo);
    }
}
