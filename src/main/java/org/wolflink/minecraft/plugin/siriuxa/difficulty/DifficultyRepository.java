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
public class DifficultyRepository extends MapRepository<Integer, TaskDifficulty> {
    public DifficultyRepository() {
        insert(TaskDifficulty.builder()
                .icon(Material.WOODEN_PICKAXE)
                .level(1)
                .name("轻松")
                .color("§a")
                .build());
        insert(TaskDifficulty.builder()
                .icon(Material.STONE_PICKAXE)
                .level(2)
                .name("常规")
                .color("§b")
                .build());
        insert(TaskDifficulty.builder()
                .icon(Material.IRON_PICKAXE)
                .level(3)
                .name("困难")
                .color("§d")
                .build());
        insert(TaskDifficulty.builder()
                .icon(Material.DIAMOND_PICKAXE)
                .level(4)
                .name("专家")
                .color("§c")
                .build());
    }

    @Override
    public Integer getPrimaryKey(TaskDifficulty difficulty) {
        return difficulty.getLevel();
    }

    /**
     * 根据难度类型和名字查找指定的难度
     */
    @Nullable
    public TaskDifficulty findByName(String name) {
        for (TaskDifficulty difficulty : findAll()) {
            if (name.equals(difficulty.getName())) return difficulty;
        }
        return null;
    }
}
