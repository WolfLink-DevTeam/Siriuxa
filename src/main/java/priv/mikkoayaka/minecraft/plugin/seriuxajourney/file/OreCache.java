package priv.mikkoayaka.minecraft.plugin.seriuxajourney.file;

import org.bukkit.Material;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.config.YamlConfig;

import java.util.Calendar;
import java.util.HashMap;

@Singleton
public class OreCache extends YamlConfig {
    public OreCache() {
        super("SeriuxaJourneyOreCache", new HashMap<>());
    }
    private String getDate(Calendar calendar) {
        return String.format("%4d%2d%2d",calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
    }
    private String getPath(Calendar calendar,Material material) {
        return getDate(calendar)+"."+material.name().toLowerCase();
    }
    private String getPath(Material material) {
        return getDate(Calendar.getInstance())+"."+material.name().toLowerCase();
    }
    public int getOreCache(Calendar calendar,Material material) {
        return get(getPath(calendar,material),0);
    }
    public int getOreCacheToday(Material material) {
        return get(getPath(material),0);
    }
    public void addOreCacheToday(Material material,int count) {
        update(getPath(material),getOreCacheToday(material)+count);
    }
}
