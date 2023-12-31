package org.wolflink.minecraft.plugin.siriuxa.monster;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.entity.EntityType;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 怪物生成器的属性类
 */
@Data
public class SpawnerAttribute {
    private final int spawnPeriodSecs;
    private final Map<EntityType, Integer> weightMap = new EnumMap<>(EntityType.class);
    private double healthMultiple;
    private double movementMultiple;
    private double damageMultiple;

    public SpawnerAttribute(@NonNull TaskDifficulty difficulty) {
        switch (difficulty.getLevel()) {
            case 1 -> { // 轻松
                healthMultiple = 0.5;
                movementMultiple = 0.8;
                damageMultiple = 0.5;
                spawnPeriodSecs = 12;
                weightMap.put(EntityType.ZOMBIE, 50);
                weightMap.put(EntityType.HUSK, 50);
                weightMap.put(EntityType.ZOMBIE_VILLAGER, 50);
                weightMap.put(EntityType.SKELETON, 20);
                weightMap.put(EntityType.STRAY, 20);
                weightMap.put(EntityType.SILVERFISH, 20);
                weightMap.put(EntityType.SPIDER, 40);
                weightMap.put(EntityType.CREEPER, 20);
            }
            case 2 -> { // 常规
                healthMultiple = 0.8;
                movementMultiple = 1.0;
                damageMultiple = 0.8;
                spawnPeriodSecs = 11;
                weightMap.put(EntityType.ZOMBIE, 50);
                weightMap.put(EntityType.HUSK, 50);
                weightMap.put(EntityType.ZOMBIE_VILLAGER, 50);
                weightMap.put(EntityType.SKELETON, 20);
                weightMap.put(EntityType.STRAY, 20);
                weightMap.put(EntityType.SILVERFISH, 20);
                weightMap.put(EntityType.SPIDER, 40);
                weightMap.put(EntityType.CREEPER, 20);
            }
            case 3 -> { // 困难
                healthMultiple = 1.05;
                movementMultiple = 1.1;
                damageMultiple = 1.05;
                spawnPeriodSecs = 9;
                weightMap.put(EntityType.ZOMBIE, 45);
                weightMap.put(EntityType.HUSK, 45);
                weightMap.put(EntityType.ZOMBIE_VILLAGER, 45);
                weightMap.put(EntityType.SKELETON, 20);
                weightMap.put(EntityType.STRAY, 20);
                weightMap.put(EntityType.SILVERFISH, 20);
                weightMap.put(EntityType.SPIDER, 40);
                weightMap.put(EntityType.CREEPER, 20);
                weightMap.put(EntityType.VEX, 4);
                weightMap.put(EntityType.VINDICATOR, 5);
                weightMap.put(EntityType.WITCH, 5);
                weightMap.put(EntityType.RABBIT, 1);
            }
            case 4 -> { // 专家
                healthMultiple = 1.25;
                movementMultiple = 1.25;
                damageMultiple = 1.25;
                spawnPeriodSecs = 8;
                weightMap.put(EntityType.ZOMBIE, 40);
                weightMap.put(EntityType.HUSK, 40);
                weightMap.put(EntityType.ZOMBIE_VILLAGER, 40);
                weightMap.put(EntityType.SKELETON, 20);
                weightMap.put(EntityType.STRAY, 20);
                weightMap.put(EntityType.SILVERFISH, 20);
                weightMap.put(EntityType.SPIDER, 40);
                weightMap.put(EntityType.CREEPER, 20);
                weightMap.put(EntityType.VINDICATOR, 5);
                weightMap.put(EntityType.VEX, 4);
                weightMap.put(EntityType.WITCH, 5);
                weightMap.put(EntityType.RABBIT, 1);
            }
            default -> {
                healthMultiple = 1.0;
                movementMultiple = 1.0;
                damageMultiple = 1.0;
                spawnPeriodSecs = 30;
                weightMap.put(EntityType.ZOMBIE, 100);
                Notifier.error("不支持的难度等级：" + difficulty.getLevel());
            }
        }
    }

    /**
     * 根据权重随机获得怪物类型
     */
    public EntityType randomType() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int totalWeight = weightMap.values().stream().reduce(0, Integer::sum);
        int randomInt = random.nextInt(totalWeight);
        for (Map.Entry<EntityType, Integer> entry : weightMap.entrySet()) {
            if (randomInt < entry.getValue()) return entry.getKey();
            randomInt -= entry.getValue();
        }
        // 不可触及
        Notifier.warn("怪物类型随机时出现问题。");
        return EntityType.ZOMBIE;
    }
}
