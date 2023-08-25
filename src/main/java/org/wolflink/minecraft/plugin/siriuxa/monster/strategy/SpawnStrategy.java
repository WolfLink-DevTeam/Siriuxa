package org.wolflink.minecraft.plugin.siriuxa.monster.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.MetadataKey;
import org.wolflink.minecraft.plugin.siriuxa.monster.SpawnerAttribute;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;

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
     * 异常重试 5 次
     */
    public void spawn(Player player) {
        spawn(player, 5);
    }

    abstract void spawn(Player player, int triedCount);

    /**
     * 为生成的怪物附加额外信息
     */
    void appendMetadata(Player player, Entity entity) {
        // 生成该怪物的玩家
        entity.setMetadata(MetadataKey.MONSTER_BELONG_PLAYER.getKey(), new FixedMetadataValue(Siriuxa.getInstance(),player.getName()));
        Task task = IOC.getBean(TaskRepository.class).findByTaskTeamPlayer(player);
        // 玩家归属的任务UUID
        if(task != null) {
            entity.setMetadata(MetadataKey.MONSTER_BELONG_TASK_UUID.getKey(), new FixedMetadataValue(Siriuxa.getInstance(),task.getTaskUuid()));
        }
    }
}
