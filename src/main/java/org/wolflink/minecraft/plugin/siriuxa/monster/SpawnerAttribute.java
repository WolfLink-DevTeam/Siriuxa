package org.wolflink.minecraft.plugin.siriuxa.monster;

import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 怪物生成器的属性类
 */
@Getter
public class SpawnerAttribute {
    private final double healthMultiple;
    private final double movementMultiple;
    private final double damageMultiple;
    private final double decideSpawnChance;
    private final Map<EntityType, Integer> weightMap = new EnumMap<>(EntityType.class);

    public SpawnerAttribute(TaskDifficulty taskDifficulty) {
        switch (taskDifficulty.getLevel()) {
            case 1 -> { // 轻松
                healthMultiple = 0.8;
                movementMultiple = 0.85;
                damageMultiple = 0.8;
                decideSpawnChance = 0.6;
                weightMap.put(EntityType.ZOMBIE, 40);
                weightMap.put(EntityType.HUSK, 40);
                weightMap.put(EntityType.ZOMBIE_VILLAGER, 40);
                weightMap.put(EntityType.SKELETON, 20);
                weightMap.put(EntityType.STRAY, 20);
                weightMap.put(EntityType.SILVERFISH, 20);
                weightMap.put(EntityType.SPIDER, 40);
                weightMap.put(EntityType.CREEPER, 20);
            }
            case 2 -> { // 常规
                healthMultiple = 1.0;
                movementMultiple = 1.0;
                damageMultiple = 1.0;
                decideSpawnChance = 0.7;
                weightMap.put(EntityType.ZOMBIE, 40);
                weightMap.put(EntityType.HUSK, 40);
                weightMap.put(EntityType.ZOMBIE_VILLAGER, 40);
                weightMap.put(EntityType.SKELETON, 20);
                weightMap.put(EntityType.STRAY, 20);
                weightMap.put(EntityType.SILVERFISH, 20);
                weightMap.put(EntityType.SPIDER, 40);
                weightMap.put(EntityType.CREEPER, 20);
                weightMap.put(EntityType.VEX, 20);
            }
            case 3 -> { // 困难
                healthMultiple = 1.5;
                movementMultiple = 1.15;
                damageMultiple = 1.2;
                decideSpawnChance = 0.8;
                weightMap.put(EntityType.ZOMBIE, 35);
                weightMap.put(EntityType.HUSK, 35);
                weightMap.put(EntityType.ZOMBIE_VILLAGER, 35);
                weightMap.put(EntityType.SKELETON, 20);
                weightMap.put(EntityType.STRAY, 20);
                weightMap.put(EntityType.SILVERFISH, 20);
                weightMap.put(EntityType.SPIDER, 40);
                weightMap.put(EntityType.CREEPER, 20);
                weightMap.put(EntityType.VEX, 20);
                weightMap.put(EntityType.VINDICATOR, 10);
                weightMap.put(EntityType.WARDEN, 3);
            }
            case 4 -> { // 专家
                healthMultiple = 2.0;
                movementMultiple = 1.3;
                damageMultiple = 1.5;
                decideSpawnChance = 0.9;
                weightMap.put(EntityType.ZOMBIE, 30);
                weightMap.put(EntityType.HUSK, 30);
                weightMap.put(EntityType.ZOMBIE_VILLAGER, 30);
                weightMap.put(EntityType.SKELETON, 20);
                weightMap.put(EntityType.STRAY, 20);
                weightMap.put(EntityType.SILVERFISH, 20);
                weightMap.put(EntityType.SPIDER, 40);
                weightMap.put(EntityType.CREEPER, 20);
                weightMap.put(EntityType.VEX, 20);
                weightMap.put(EntityType.VINDICATOR, 10);
                weightMap.put(EntityType.WARDEN, 5);
            }
            default -> {
                healthMultiple = 1.0;
                movementMultiple = 1.0;
                damageMultiple = 1.0;
                decideSpawnChance = 0;
                weightMap.put(EntityType.ZOMBIE, 100);
                weightMap.put(EntityType.SKELETON, 40);
                weightMap.put(EntityType.SPIDER, 40);
                weightMap.put(EntityType.CREEPER, 20);
                Notifier.error("不支持的难度等级：" + taskDifficulty.getLevel());
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
