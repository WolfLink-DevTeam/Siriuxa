package org.wolflink.minecraft.plugin.siriuxa.task.listeners.orecheck;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.file.database.OreDB;

import java.util.Calendar;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * 矿物价值类
 */
@Singleton
public class OreValues {
    /**
     * 材质 - 相对于铜块的价值
     */
    private static final EnumMap<Material, Double> valueMap = new EnumMap<>(Material.class);
    /**
     * 整体价值倍率
     */
    private static final double MULTIPLE = 25.0;
    /**
     * 最少记录池数量
     */
    private static final int MIN_POOL_SIZE = 100;
    /**
     * 最大记录天数
     */
    private static final int MAX_RECORD_DATE = 30;

    static {
        valueMap.put(Material.RAW_COPPER_BLOCK, 0.5);
        valueMap.put(Material.COPPER_BLOCK, 0.95);
        valueMap.put(Material.COAL_BLOCK, 1.2);
        valueMap.put(Material.RAW_IRON_BLOCK, 1.0);
        valueMap.put(Material.IRON_BLOCK, 2.25);
        valueMap.put(Material.RAW_GOLD_BLOCK, 1.2);
        valueMap.put(Material.GOLD_BLOCK, 2.5);
        valueMap.put(Material.LAPIS_BLOCK, 0.75);
        valueMap.put(Material.REDSTONE_BLOCK, 0.75);
        valueMap.put(Material.DIAMOND_BLOCK, 7.0);
        valueMap.put(Material.EMERALD_BLOCK, 5.0);
    }

    /**
     * 矿物数量历史记录
     */
    private final EnumMap<Material, Integer> cacheMap = new EnumMap<>(Material.class);
    /**
     * 今日矿物数量记录
     */
    private final EnumMap<Material, Integer> todayMap = new EnumMap<>(Material.class);
    @Inject
    private OreDB oreDB;
    private int totalCache = 0;

    public OreValues() {
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {
            for (Material material : getOreMaterials()) {
                Calendar calendar = Calendar.getInstance();
                int count = 0;
                for (int i = 0; i < MAX_RECORD_DATE; i++) {
                    count += oreDB.getOreCache(calendar, material);
                    calendar.add(Calendar.DATE, -1);
                }
                Notifier.debug("获取到矿物记录数据：" + material.name().toLowerCase() + "，数量总计：" + count);
                cacheMap.put(material, count);
                totalCache += count;
            }
        });
    }

    public void doRecord(Material material) {
        if (todayMap.containsKey(material)) todayMap.put(material, todayMap.get(material) + 1);
        else todayMap.put(material, 1);
    }

    public void doSave() {
        for (Map.Entry<Material, Integer> entry : todayMap.entrySet()) {
            oreDB.addOreCacheToday(entry.getKey(), entry.getValue());
        }
    }

    public Set<Material> getOreMaterials() {
        return valueMap.keySet();
    }

    /**
     * 获取当前价值百分比(只根据历史数据进行判定) [0.2,1.2]
     */
    public double getValuePercent(Material material) {
        if (totalCache <= MIN_POOL_SIZE) return 1 - (1.0 / getOreMaterials().size());
        return 1.2 - (cacheMap.get(material) / (double) totalCache);
    }

    public double getOreValue(Material material) {
        return (valueMap.get(material) * MULTIPLE) * getValuePercent(material);
    }
}
