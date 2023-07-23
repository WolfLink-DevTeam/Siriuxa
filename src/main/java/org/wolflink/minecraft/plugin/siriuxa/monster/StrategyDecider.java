package org.wolflink.minecraft.plugin.siriuxa.monster;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.api.ISwitchable;
import org.wolflink.minecraft.plugin.siriuxa.monster.strategy.OceanSpawnStrategy;
import org.wolflink.minecraft.plugin.siriuxa.monster.strategy.PlayerFocusSpawnStrategy;
import org.wolflink.minecraft.plugin.siriuxa.monster.strategy.SpawnStrategy;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 为每个任务分配一个决策者
 * 遍历并决策任务中玩家当前的刷怪机制
 */
public class StrategyDecider implements ISwitchable {

    private final SpawnerAttribute spawnerAttribute;
    private final Task task;
    private final int spawnPeriodSecs;
    public StrategyDecider(Task task,int spawnPeriodSecs) {
        this.task = task;
        this.spawnPeriodSecs = spawnPeriodSecs;
        spawnerAttribute = new SpawnerAttribute(task.getTaskDifficulty());
        strategyList = new ArrayList<>(){{
            add(new OceanSpawnStrategy(spawnerAttribute));
            add(new PlayerFocusSpawnStrategy(spawnerAttribute));
        }};
    }

    private final SubScheduler subScheduler = new SubScheduler();
    /**
     * 决策周期(秒)
     */
    private final int DECIDE_PERIOD_SECS = 60;
    /**
     * 玩家当前应用的决策
     */
    private final Map<UUID, SpawnStrategy> playerStrategyMap = new ConcurrentHashMap<>();
    /**
     * 优先级从上往下，最上方的最优先进行决策
     */
    private final List<SpawnStrategy> strategyList;
    @Override
    public void enable() {
        subScheduler.runTaskTimerAsync(
                this::updateStrategyMap,
                20 * DECIDE_PERIOD_SECS,
                20 * DECIDE_PERIOD_SECS
        );
        subScheduler.runTaskTimerAsync(
                this::spawnTask,
                20L * spawnPeriodSecs,
                20L * spawnPeriodSecs
        );
    }
    private void updateStrategyMap() {
        playerStrategyMap.clear();
        // 确保线程安全
        List<Player> list = new ArrayList<>(task.getTaskPlayers());
        for (Player player : list) {
            playerStrategyMap.put(player.getUniqueId(),decide(player));
        }
    }
    private void spawnTask() {
        for (Map.Entry<UUID,SpawnStrategy> entry : playerStrategyMap.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if(player == null || !player.isOnline()) continue;
            entry.getValue().spawn(player);
        }
    }

    /**
     * 选择一个最合适该玩家的策略
     */
    @NonNull
    private SpawnStrategy decide(Player player) {
        for (SpawnStrategy spawnStrategy : strategyList) {
            if(spawnStrategy.isApplicable(player)) return spawnStrategy;
        }
        return strategyList.get(0);
    }

    @Override
    public void disable() {
        subScheduler.cancelAllTasks();
    }
}
