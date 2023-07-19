package org.wolflink.minecraft.plugin.siriuxa.file.database;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.DateAPI;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import java.io.File;
import java.util.Calendar;


/**
 * 记录矿物数据
 */
@Singleton
public class OreDB extends FileDB {
    @Inject
    private DateAPI dateAPI;

    public OreDB() {
        super("ore");
    }

    @NonNull
    private String getPath(@NonNull Calendar calendar) {
        return dateAPI.getDate(calendar) + ".yml";
    }

    public int getOreCache(@NonNull Calendar calendar, @NonNull Material material) {
        FileConfiguration fileConfiguration = getFileConfiguration(new File(folder, getPath(calendar)));
        if (fileConfiguration == null) {
            Notifier.debug("尝试获取" + material.name() + "数据失败。");
            return 0;
        }
        return fileConfiguration.getInt(material.name().toLowerCase(), 0);
    }

    public int getOreCacheToday(@NonNull Material material) {
        return getOreCache(Calendar.getInstance(), material);
    }

    public void addOreCacheToday(@NonNull Material material, int count) {
        File file = new File(folder, getPath(Calendar.getInstance()));
        FileConfiguration fileConfiguration = getFileConfiguration(file);
        if (fileConfiguration == null) fileConfiguration = createAndLoad(file);
        fileConfiguration.set(material.name().toLowerCase(), getOreCacheToday(material) + count);
        try {
            fileConfiguration.save(file);
            Notifier.debug(material.name() + "数据已保存。");
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.debug("尝试保存" + material.name() + "数据失败。");
        }
    }
}
