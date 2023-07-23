package org.wolflink.minecraft.plugin.siriuxa.difficulty;

import org.bukkit.Material;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.database.repository.MapRepository;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * 任务难度等级 -> 任务难度记录类
 */
@Singleton
public class DifficultyRepository extends MapRepository<DifficultyKey, TaskDifficulty> {
    public DifficultyRepository() {
        insert(ExplorationDifficulty.builder()
                .icon(Material.WOODEN_PICKAXE)
                .level(1)
                .name("轻松")
                .color("§a")
                .wheatCost(50)
                .wheatSupply(200)
                .baseWheatLoss(0.1)
                .wheatLostAcceleratedSpeed(0.08)
                .hurtWheatCost(2.0)
                .hurtDamageMultiple(1.0)
                .bringSlotAmount(10)
                .wheatGainPercent(0.1)
                .expGainPercent(1.0)
                .build());
        insert(ExplorationDifficulty.builder()
                .icon(Material.STONE_PICKAXE)
                .level(2)
                .name("常规")
                .color("§b")
                .wheatCost(80)
                .wheatSupply(200)
                .baseWheatLoss(0.1)
                .wheatLostAcceleratedSpeed(0.12)
                .hurtWheatCost(3.0)
                .hurtDamageMultiple(1.1)
                .bringSlotAmount(12)
                .wheatGainPercent(0.15)
                .expGainPercent(1.0)
                .build());
        insert(ExplorationDifficulty.builder()
                .icon(Material.IRON_PICKAXE)
                .level(3)
                .name("困难")
                .color("§d")
                .wheatCost(120)
                .wheatSupply(200)
                .baseWheatLoss(0.1)
                .wheatLostAcceleratedSpeed(0.16)
                .hurtWheatCost(3.0)
                .hurtDamageMultiple(1.15)
                .bringSlotAmount(14)
                .wheatGainPercent(0.2)
                .expGainPercent(1.0)
                .build());
        insert(ExplorationDifficulty.builder()
                .icon(Material.DIAMOND_PICKAXE)
                .level(4)
                .name("专家")
                .color("§c")
                .wheatCost(160)
                .wheatSupply(200)
                .baseWheatLoss(0.1)
                .wheatLostAcceleratedSpeed(0.2)
                .hurtWheatCost(4.0)
                .hurtDamageMultiple(1.2)
                .bringSlotAmount(16)
                .wheatGainPercent(0.25)
                .expGainPercent(1.0)
                .build());
    }

    @Override
    public DifficultyKey getPrimaryKey(TaskDifficulty taskDifficulty) {
        return new DifficultyKey(taskDifficulty.getClass(), taskDifficulty.getLevel());
    }
    @Nullable
    public ExplorationDifficulty findByName(String name) {
        for (ExplorationDifficulty explorationDifficulty : findByType(ExplorationDifficulty.class)) {
            if(name.equals(explorationDifficulty.getName()))return explorationDifficulty;
        }
        return null;
    }

    public <T extends TaskDifficulty> Collection<T> findByType(Class<T> clazz) {
        return findAll()
                .stream()
                .filter(taskDifficulty -> taskDifficulty.getClass().equals(clazz))
                .map(taskDifficulty -> (T) taskDifficulty)
                .sorted(Comparator.comparingInt(o -> o.level))
                .collect(Collectors.toList());
    }
}
