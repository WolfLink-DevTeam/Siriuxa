package org.wolflink.minecraft.plugin.siriuxa.task.listeners.farmcheck;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.file.database.CropDB;

import java.util.Calendar;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * 怪物价值类
 */
@Singleton
public class FarmValues {
    /**
     * 作物类型 - 相对于小麦的价值
     */
    private static final EnumMap<Material, Double> valueMap = new EnumMap<>(Material.class);
    /**
     * 整体价值倍率
     */
    private static final double MULTIPLE = 9.0;
    /**
     * 最少记录池数量
     */
    private static final int MIN_POOL_SIZE = 300;
    /**
     * 最大记录天数
     */
    private static final int MAX_RECORD_DATE = 30;

    static {
        valueMap.put(Material.WHEAT, 1.0);
        valueMap.put(Material.CARROT, 1.2);
        valueMap.put(Material.POTATO, 1.2);
        valueMap.put(Material.BEETROOT, 1.5);
        valueMap.put(Material.MELON_SLICE, 1.3);
    }

    /**
     * 收获数量历史记录
     */
    private final EnumMap<Material, Integer> cacheMap = new EnumMap<>(Material.class);
    /**
     * 今日收获数量记录
     */
    private final EnumMap<Material, Integer> todayMap = new EnumMap<>(Material.class);
    @Inject
    private CropDB cropDB;
    private int totalCache = 0;

    public FarmValues() {
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {
            for (Material crop : getCropTypes()) {
                Calendar calendar = Calendar.getInstance();
                int count = 0;
                for (int i = 0; i < MAX_RECORD_DATE; i++) {
                    count += cropDB.getCropCache(calendar, crop);
                    calendar.add(Calendar.DATE, -1);
                }
                Notifier.debug("获取到作物记录数据：" + crop.name().toLowerCase() + "，数量总计：" + count);
                cacheMap.put(crop, count);
                totalCache += count;
            }
        });
    }

    public void doRecord(Material crop) {
        if (todayMap.containsKey(crop)) todayMap.put(crop, todayMap.get(crop) + 1);
        else todayMap.put(crop, 1);
    }

    public void doSave() {
        for (Map.Entry<Material, Integer> entry : todayMap.entrySet()) {
            cropDB.addCropCacheToday(entry.getKey(), entry.getValue());
        }
    }

    public Set<Material> getCropTypes() {
        return valueMap.keySet();
    }

    /**
     * 获取当前价值百分比(只根据历史数据进行判定) [0.2,1.2]
     */
    public double getValuePercent(Material crop) {
        if (totalCache <= MIN_POOL_SIZE) return 1 - (1.0 / getCropTypes().size());
        return 1.2 - (cacheMap.get(crop) / (double) totalCache);
    }

    public double getCropValue(Material crop) {
        return (valueMap.get(crop) * MULTIPLE) * getValuePercent(crop);
    }
}
