package priv.mikkoayaka.minecraft.plugin.seriuxajourney.file;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.config.YamlConfig;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

@Singleton
public class Config extends YamlConfig {
    public Config() {
        super("SeriuxaJourney-Config", ConfigProjection.asMap());
    }

    @NonNull
    public <T> T get(ConfigProjection configProjection) {
        T result = super.get(configProjection.getPath());
        if(result == null)return (T) configProjection.getDefaultValue();
        else return result;
    }
    @Nullable
    public Location getLobbyLocation() {
        String worldName = get(ConfigProjection.LOBBY_WORLD_NAME);
        World lobbyWorld = Bukkit.getWorld(worldName);
        if(lobbyWorld == null) return null;
        String xyzString = get(ConfigProjection.LOBBY_LOCATION);
        String[] xyz = xyzString.split(" ");
        Location location;
        try {
            location = new Location(lobbyWorld,Integer.parseInt(xyz[0]),Integer.parseInt(xyz[1]),Integer.parseInt(xyz[2]));
        } catch (Exception e) {
            Notifier.warn("配置文件 LobbyLocation 相关配置出现异常。");
            return null;
        }
        return location;
    }
}
