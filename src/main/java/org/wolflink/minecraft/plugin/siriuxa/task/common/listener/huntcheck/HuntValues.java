package org.wolflink.minecraft.plugin.siriuxa.task.common.listener.huntcheck;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.file.database.HuntDB;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;

import java.util.Calendar;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * 怪物价值类
 */
@Singleton
public class HuntValues {
    /**
     * 怪物类型 - 相对于僵尸的价值
     */
    private static final EnumMap<EntityType, Double> valueMap = new EnumMap<>(EntityType.class);

    static {
        valueMap.put(EntityType.ZOMBIE, 1.0);
        valueMap.put(EntityType.SPIDER, 1.15);
        valueMap.put(EntityType.SKELETON, 1.25);
        valueMap.put(EntityType.CREEPER, 2.0);
        valueMap.put(EntityType.DROWNED, 1.0);
        valueMap.put(EntityType.ZOMBIE_VILLAGER, 1.0);
        valueMap.put(EntityType.HUSK, 1.0);
        valueMap.put(EntityType.STRAY, 1.3);
        valueMap.put(EntityType.SILVERFISH, 0.5);
        valueMap.put(EntityType.VEX, 5.0);
        valueMap.put(EntityType.ENDERMAN, 3.0);
        valueMap.put(EntityType.VINDICATOR, 1.2);
        valueMap.put(EntityType.ENDERMITE,25.0);
        valueMap.put(EntityType.CAVE_SPIDER,1.5);
    }

    @Inject
    private HuntDB huntDB;
    /**
     * 击杀怪物数量历史记录
     */
    private final EnumMap<EntityType, Integer> cacheMap = new EnumMap<>(EntityType.class);
    private int totalCache = 0;
    /**
     * 今日击杀怪物数量记录
     */
    private final EnumMap<EntityType, Integer> todayMap = new EnumMap<>(EntityType.class);
    /**
     * 整体价值倍率
     */
    private static final double MULTIPLE = 4.0;
    /**
     * 最少记录池数量
     */
    private static final int MIN_POOL_SIZE = 300;
    /**
     * 最大记录天数
     */
    private static final int MAX_RECORD_DATE = 30;

    public HuntValues() {
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {
            for (EntityType entityType : getMonsterTypes()) {
                Calendar calendar = Calendar.getInstance();
                int count = 0;
                for (int i = 0; i < MAX_RECORD_DATE; i++) {
                    count += huntDB.getHuntCache(calendar, entityType);
                    calendar.add(Calendar.DATE, -1);
                }
                Notifier.debug("获取到怪物记录数据：" + entityType.name().toLowerCase() + "，数量总计：" + count);
                cacheMap.put(entityType, count);
                totalCache += count;
            }
        });
    }

    public void doRecord(EntityType entityType) {
        if (todayMap.containsKey(entityType)) todayMap.put(entityType, todayMap.get(entityType) + 1);
        else todayMap.put(entityType, 1);
    }

    public void doSave() {
        for (Map.Entry<EntityType, Integer> entry : todayMap.entrySet()) {
            huntDB.addHuntCacheToday(entry.getKey(), entry.getValue());
        }
    }

    public Set<EntityType> getMonsterTypes() {
        return valueMap.keySet();
    }

    /**
     * 获取当前价值百分比(只根据历史数据进行判定) [0.2,1.2]
     */
    public double getValuePercent(EntityType entityType) {
        if (totalCache <= MIN_POOL_SIZE) return 1 - (1.0 / getMonsterTypes().size());
        return 1.2 - (cacheMap.get(entityType) / (double) totalCache);
    }

    public double getHuntValue(EntityType entityType) {
        return (valueMap.get(entityType) * MULTIPLE) * getValuePercent(entityType);
    }
}
