package priv.mikkoayaka.minecraft.plugin.seriuxajourney.file;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.config.YamlConfig;

@Singleton
public class Config extends YamlConfig {
    public Config() {
        super("SeriuxaJourneyConfig", ConfigProjection.asMap());
    }

    @NonNull
    public <T> T get(ConfigProjection configProjection) {
        T result = super.get(configProjection.getPath());
        if(result == null)return (T) configProjection.getDefaultValue();
        else return result;
    }
    @NonNull
    public Location getLobbyLocation() {
        String worldName = get(ConfigProjection.LOBBY_WORLD_NAME);
        World lobbyWorld = Bukkit.getWorld(worldName);
        if(lobbyWorld == null) throw new NullPointerException("配置文件中没有设置 LobbyLocation");
        String xyzString = get(ConfigProjection.LOBBY_LOCATION);
        String[] xyz = xyzString.split(" ");
        Location location;
        try {
            location = new Location(lobbyWorld,Integer.parseInt(xyz[0]),Integer.parseInt(xyz[1]),Integer.parseInt(xyz[2]));
            return location;
        } catch (Exception e) {
            System.out.println("配置文件 LobbyLocation 相关配置出现异常。");
        }
        throw new NullPointerException("配置文件中没有设置 LobbyLocation");
    }
    public int getNextRegionIndex() {
        int value = get(ConfigProjection.EXPLORATION_REGION_INDEX);
        update(ConfigProjection.EXPLORATION_REGION_INDEX.getPath(),value+1);
        return value;
    }
}
