package org.wolflink.minecraft.plugin.siriuxa.monster.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.monster.SpawnerAttribute;

/**
 * 怪物生成策略
 * 通过一定的算法在给定玩家附近生成一只怪物
 */
@Getter
@AllArgsConstructor
public abstract class SpawnStrategy {
    private final SpawnerAttribute spawnerAttribute;
    /**
     * 判断玩家是否适合应用该刷怪策略
     */
    public abstract boolean isApplicable(Player player);
    /**
     * 具体的刷怪算法
     * 异步计算，同步生成
     */
    public abstract void spawn(Player player);
}
