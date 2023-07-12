package priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty;

import org.bukkit.Material;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.database.repository.MapRepository;

/**
 * 任务难度等级 -> 任务难度记录类
 */
@Singleton
public class DifficultyRepository extends MapRepository<Integer,TaskDifficulty> {
    public DifficultyRepository() {
        insert(TaskDifficulty.builder()
                .icon(Material.WOODEN_PICKAXE)
                .level(1)
                .name("轻松")
                .color("§a")
                .wheatCost(50)
                .wheatSupply(200)
                .wheatLostAcceleratedSpeed(0.04)
                .hurtWheatCost(2.0)
                .hurtDamageMultiple(1.0)
                .bringSlotAmount(6)
                .wheatGainPercent(0.1)
                .expGainPercent(0.5)
                .build());
        insert(TaskDifficulty.builder()
                .icon(Material.STONE_PICKAXE)
                .level(2)
                .name("常规")
                .color("§b")
                .wheatCost(80)
                .wheatSupply(200)
                .wheatLostAcceleratedSpeed(0.06)
                .hurtWheatCost(3.0)
                .hurtDamageMultiple(1.25)
                .bringSlotAmount(9)
                .wheatGainPercent(0.16)
                .expGainPercent(0.65)
                .build());
        insert(TaskDifficulty.builder()
                .icon(Material.IRON_PICKAXE)
                .level(3)
                .name("困难")
                .color("§d")
                .wheatCost(120)
                .wheatSupply(200)
                .wheatLostAcceleratedSpeed(0.08)
                .hurtWheatCost(3.0)
                .hurtDamageMultiple(1.5)
                .bringSlotAmount(12)
                .wheatGainPercent(0.24)
                .expGainPercent(0.8)
                .build());
        insert(TaskDifficulty.builder()
                .icon(Material.DIAMOND_PICKAXE)
                .level(4)
                .name("专家")
                .color("§c")
                .wheatCost(160)
                .wheatSupply(200)
                .wheatLostAcceleratedSpeed(0.10)
                .hurtWheatCost(4.0)
                .hurtDamageMultiple(1.75)
                .bringSlotAmount(15)
                .wheatGainPercent(0.35)
                .expGainPercent(1.0)
                .build());
    }
    @Override
    public Integer getPrimaryKey(TaskDifficulty taskDifficulty) {
        return taskDifficulty.level();
    }
}
