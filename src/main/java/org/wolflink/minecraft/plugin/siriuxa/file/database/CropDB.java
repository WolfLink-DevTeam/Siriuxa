package org.wolflink.minecraft.plugin.siriuxa.file.database;


import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.DateAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;

import java.io.File;
import java.util.Calendar;

/**
 * 记录收获作物数据
 */
@Singleton
public class CropDB extends FileDB {
    @Inject
    private DateAPI dateAPI;

    public CropDB() {
        super("crop");
    }

    @NonNull
    private String getPath(@NonNull Calendar calendar) {
        return dateAPI.getDate(calendar) + ".yml";
    }

    public int getCropCache(@NonNull Calendar calendar, @NonNull Material crop) {
        FileConfiguration fileConfiguration = getFileConfiguration(new File(folder, getPath(calendar)));
        if (fileConfiguration == null) {
            Notifier.debug("尝试获取" + crop.name() + "数据失败。");
            return 0;
        }
        return fileConfiguration.getInt(crop.name().toLowerCase(), 0);
    }

    public int getCropCacheToday(@NonNull Material crop) {
        return getCropCache(Calendar.getInstance(), crop);
    }

    public void addCropCacheToday(@NonNull Material crop, int count) {
        File file = new File(folder, getPath(Calendar.getInstance()));
        FileConfiguration fileConfiguration = getFileConfiguration(file);
        if (fileConfiguration == null) fileConfiguration = createAndLoad(file);
        fileConfiguration.set(crop.name().toLowerCase(), getCropCacheToday(crop) + count);
        try {
            fileConfiguration.save(file);
            Notifier.debug(crop.name() + "数据已保存。");
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.debug("尝试保存" + crop.name() + "数据失败。");
        }
    }
}
