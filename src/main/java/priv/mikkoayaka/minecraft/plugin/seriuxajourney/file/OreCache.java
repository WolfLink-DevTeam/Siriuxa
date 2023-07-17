package priv.mikkoayaka.minecraft.plugin.seriuxajourney.file;

import org.bukkit.Material;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.config.YamlConfig;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.DateAPI;

import java.util.Calendar;
import java.util.HashMap;

@Singleton
public class OreCache extends YamlConfig {
    @Inject
    private DateAPI dateAPI;
    public OreCache() {
        super("SeriuxaJourneyOreCache", new HashMap<>());
    }

    private String getPath(Material material) {
        return dateAPI.getDate(Calendar.getInstance())+"."+material.name().toLowerCase();
    }
    public String getPath(Calendar calendar, Material material) {
        return dateAPI.getDate(calendar)+"."+material.name().toLowerCase();
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
