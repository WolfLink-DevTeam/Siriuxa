package org.wolflink.minecraft.plugin.siriuxa.file.database;

import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.DateAPI;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import java.io.File;
import java.util.Calendar;


/**
 * 记录狩猎数据
 */
@Singleton
public class HuntDB extends FileDB {
    @Inject
    private DateAPI dateAPI;

    public HuntDB() {
        super("hunt");
    }

    @NonNull
    private String getPath(@NonNull Calendar calendar) {
        return dateAPI.getDate(calendar) + ".yml";
    }

    public int getHuntCache(@NonNull Calendar calendar, @NonNull EntityType entityType) {
        FileConfiguration fileConfiguration = getFileConfiguration(new File(folder, getPath(calendar)));
        if (fileConfiguration == null) {
            Notifier.debug("尝试获取" + entityType.name() + "数据失败。");
            return 0;
        }
        return fileConfiguration.getInt(entityType.name().toLowerCase(), 0);
    }

    public int getHuntCacheToday(@NonNull EntityType entityType) {
        return getHuntCache(Calendar.getInstance(), entityType);
    }

    public void addHuntCacheToday(@NonNull EntityType entityType, int count) {
        File file = new File(folder, getPath(Calendar.getInstance()));
        FileConfiguration fileConfiguration = getFileConfiguration(file);
        if (fileConfiguration == null) fileConfiguration = createAndLoad(file);
        fileConfiguration.set(entityType.name().toLowerCase(), getHuntCacheToday(entityType) + count);
        try {
            fileConfiguration.save(file);
            Notifier.debug(entityType.name() + "数据已保存。");
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.debug("尝试保存" + entityType.name() + "数据失败。");
        }
    }
}
