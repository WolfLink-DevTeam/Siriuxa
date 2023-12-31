package org.wolflink.minecraft.plugin.siriuxa.monster;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.api.IStatus;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.monster.strategy.OceanSpawnStrategy;
import org.wolflink.minecraft.plugin.siriuxa.monster.strategy.PlayerFocusSpawnStrategy;
import org.wolflink.minecraft.plugin.siriuxa.monster.strategy.SpawnStrategy;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 为每个任务分配一个决策者
 * 遍历并决策任务中玩家当前的刷怪机制
 */
public class StrategyDecider implements IStatus {

    /**
     * 决策周期(秒)
     */
    private static final int DECIDE_PERIOD_SECS = 10;
    /**
     * 判断抱团的半径(格)
     */
    private static final int HUDDLE_RADIUS = 8;
    private final SpawnerAttribute spawnerAttribute;
    private final Task task;
    private final int spawnPeriodSecs;
    private final SubScheduler subScheduler = new SubScheduler();
    /**
     * 玩家当前应用的决策
     */
    private final Map<UUID, SpawnStrategy> playerStrategyMap = new ConcurrentHashMap<>();
    /**
     * 优先级从上往下，最上方的最优先进行决策
     */
    private final List<SpawnStrategy> strategyList;

    public StrategyDecider(Task task) {
        this.task = task;
        spawnerAttribute = new SpawnerAttribute(task.getTaskDifficulty());
        this.spawnPeriodSecs = spawnerAttribute.getSpawnPeriodSecs();
        strategyList = List.of(new OceanSpawnStrategy(spawnerAttribute), new PlayerFocusSpawnStrategy(spawnerAttribute));
    }

    @Override
    public void enable() {
        Notifier.broadcastChat(task.getTaskPlayers(), "§c怪物们将在90秒后来袭，请做好准备...");
        Notifier.broadcastSound(task.getTaskPlayers(), Sound.ENTITY_VILLAGER_NO, 1f, 0.8f);
        subScheduler.runTaskLaterAsync(() -> {
            subScheduler.runTaskTimerAsync(
                    this::updateStrategyMap,
                    20L * DECIDE_PERIOD_SECS,
                    20L * DECIDE_PERIOD_SECS
            );
            subScheduler.runTaskTimerAsync(
                    this::spawnTask,
                    20L * spawnPeriodSecs,
                    20L * spawnPeriodSecs
            );
            subScheduler.runTaskTimerAsync(this::updateAttribute,
                    20L * 60, 20L * 60);
            Notifier.broadcastChat(task.getTaskPlayers(), "§c它们来了！");
            Notifier.broadcastSound(task.getTaskPlayers(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1f, 1f);
        }, 20L * 90);
    }

    private void updateStrategyMap() {
        playerStrategyMap.clear();
        // 确保线程安全
        List<Player> list = new ArrayList<>(task.getTaskPlayers());
        for (Player player : list) {
            SpawnStrategy strategy = decide(player);
            playerStrategyMap.put(player.getUniqueId(), strategy);
        }
    }

    /**
     * 每1分钟 +0.8% 血量
     * 每1分钟 +1.2% 攻击
     */
    private void updateAttribute() {
        spawnerAttribute.setHealthMultiple(spawnerAttribute.getHealthMultiple() + 0.008);
        spawnerAttribute.setDamageMultiple(spawnerAttribute.getDamageMultiple() + 0.012);
    }

    private double getEfficiencyReduction(int playerAmount) {
        return 1 - (1.25 * Math.log(playerAmount) + 1.0) / playerAmount;
    }

    private int getHuddlePlayersAmount(Location location) {
        int amount = 0;
        for (Player player : task.getTaskPlayers()) {
            if (player.getWorld() != location.getWorld()) continue;
            if (player.getLocation().distance(location) <= HUDDLE_RADIUS) amount++;
        }
        return amount;
    }

    /**
     * 多位玩家抱团时降低刷怪效率
     */
    private void spawnTask() {
        for (Map.Entry<UUID, SpawnStrategy> entry : playerStrategyMap.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline() || player.getGameMode().equals(GameMode.SPECTATOR)) continue;
            Location location = player.getLocation();
            int nearbyPlayerAmount = getHuddlePlayersAmount(location);
            if (Math.random() >= getEfficiencyReduction(nearbyPlayerAmount)) entry.getValue().spawn(player);
        }
    }

    /**
     * 选择一个最合适该玩家的策略
     */
    @NonNull
    private SpawnStrategy decide(Player player) {
        for (SpawnStrategy spawnStrategy : strategyList) {
            if (spawnStrategy.isApplicable(player)) return spawnStrategy;
        }
        return strategyList.get(0);
    }

    @Override
    public void disable() {
        subScheduler.cancelAllTasks();
    }
}
