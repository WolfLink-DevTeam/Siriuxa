package org.wolflink.minecraft.plugin.siriuxa.difficulty;

import org.bukkit.Material;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.database.repository.MapRepository;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;

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
                .wheatCost(40)
                .wheatSupply(200)
                .baseWheatLoss(0.1)
                .wheatLostAcceleratedSpeed(0.24)
                .hurtWheatCost(1.5)
                .hurtDamageMultiple(0.5)
                .rewardMultiple(0.75)
                .bringSlotAmount(8)
                .build());
        insert(ExplorationDifficulty.builder()
                .icon(Material.STONE_PICKAXE)
                .level(2)
                .name("常规")
                .color("§b")
                .wheatCost(60)
                .wheatSupply(200)
                .baseWheatLoss(0.1)
                .wheatLostAcceleratedSpeed(0.28)
                .hurtWheatCost(2.25)
                .hurtDamageMultiple(1.0)
                .rewardMultiple(1.0)
                .bringSlotAmount(10)
                .build());
        insert(ExplorationDifficulty.builder()
                .icon(Material.IRON_PICKAXE)
                .level(3)
                .name("困难")
                .color("§d")
                .wheatCost(80)
                .wheatSupply(200)
                .baseWheatLoss(0.1)
                .wheatLostAcceleratedSpeed(0.32)
                .hurtWheatCost(2.25)
                .hurtDamageMultiple(1.1)
                .rewardMultiple(1.6)
                .bringSlotAmount(12)
                .build());
        insert(ExplorationDifficulty.builder()
                .icon(Material.DIAMOND_PICKAXE)
                .level(4)
                .name("专家")
                .color("§c")
                .wheatCost(100)
                .wheatSupply(200)
                .baseWheatLoss(0.1)
                .wheatLostAcceleratedSpeed(0.36)
                .hurtWheatCost(3.0)
                .hurtDamageMultiple(1.5)
                .rewardMultiple(2.2)
                .bringSlotAmount(15)
                .build());
    }

    @Override
    public DifficultyKey getPrimaryKey(TaskDifficulty wheatTaskDifficulty) {
        return new DifficultyKey(wheatTaskDifficulty.getClass(), wheatTaskDifficulty.getLevel());
    }

    /**
     * 根据难度类型和名字查找指定的难度
     */
    @Nullable
    public <T extends TaskDifficulty> T findByName(Class<T> clazz,String name) {
        for (T difficulty : findByType(clazz)) {
            if (name.equals(difficulty.getName())) return difficulty;
        }
        return null;
    }

    /**
     * 根据难度类型查找所有包含的难度
     */
    public <T extends TaskDifficulty> Collection<T> findByType(Class<T> clazz) {
        return findAll()
                .stream()
                .filter(taskDifficulty -> taskDifficulty.getClass().equals(clazz))
                .map(taskDifficulty -> (T) taskDifficulty)
                .sorted(Comparator.comparingInt(o -> o.level))
                .toList();
    }
}
