package org.wolflink.minecraft.plugin.siriuxa.monster.deprecated;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.ISwitchable;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 任务怪物生成器
 * 主要负责处理与任务相关的怪物生成
 */
public class TaskMonsterSpawner implements ISwitchable {

    @NonNull
    private final Task task;

    private final SpawnerAttribute spawnerAttribute;

    private int spawnTaskId = -1;

    public TaskMonsterSpawner(@NonNull Task task) {
        this.task = task;
        spawnerAttribute = new SpawnerAttribute(task.getTaskDifficulty());
    }

    private final int MIN_RADIUS = 12;
    private final int MAX_RADIUS = 24;
    private final int MIN_HEIGHT = -12;
    private final int MAX_HEIGHT = 24;

    private void startSpawnMob() {
        if (spawnTaskId == -1) {
            spawnTaskId = spawnMobTask(MIN_RADIUS, MAX_RADIUS, MIN_HEIGHT, MAX_HEIGHT).getTaskId();
        }
    }

    private void stopSpawnMob() {
        if (spawnTaskId != -1) {
            Bukkit.getScheduler().cancelTask(spawnTaskId);
            spawnTaskId = -1;
        }
    }

    private @NonNull BukkitTask spawnMobTask(int minRadius, int maxRadius, int minHeight, int maxHeight) {
        Plugin plugin = Siriuxa.getInstance();
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            task.getTaskPlayers().forEach(p -> spawnMobAroundPlayer(minRadius, maxRadius, minHeight, maxHeight, p));
        }, 20 * 15L, 20 * 15L);
    }

    /**
     * 在玩家周围生成一只怪物
     */
    private void spawnMobAroundPlayer(int minRadius, int maxRadius, int minHeight, int maxHeight, @NonNull Player player) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Location loc = player.getLocation();
        World world = loc.getWorld();
        assert world != null;
        if (!isDecidedToSpawn(spawnerAttribute.getDecideSpawnChance(), random)) return;
        if (isMobCountOverLimit(maxRadius, loc)) return;

        double r = random.nextInt(maxRadius - minRadius);
        double x = loc.getX() + minRadius + r;
        double z = loc.getZ() + minRadius + r;
        double y = loc.getY() + maxHeight;
        Location spawnLoc = new Location(world, x, y, z);
        Block spawnBlock = spawnLoc.getBlock();
        while (!spawnBlock.isEmpty()) {
            spawnBlock = spawnBlock.getRelative(0, -1, 0);
            if (spawnBlock.getY() < -60) return;
            if (spawnBlock.getY() < y + minHeight) return; // 没有找到合适的位置，放弃生成
            spawnLoc.setY(spawnBlock.getY());
            spawnBlock = spawnLoc.getBlock();
        }

        EntityType entityType = spawnerAttribute.randomType();
        Monster monster = (Monster) world.spawnEntity(spawnLoc, entityType);
        AttributeInstance maxHealth = monster.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance movementSpeed = monster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        AttributeInstance attackDamage = monster.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (maxHealth != null) {
            maxHealth.setBaseValue(maxHealth.getBaseValue() * spawnerAttribute.getHealthMultiple());
            monster.setHealth(maxHealth.getBaseValue());
        }
        if (movementSpeed != null)
            movementSpeed.setBaseValue(movementSpeed.getBaseValue() * spawnerAttribute.getMovementMultiple());
        if (attackDamage != null)
            attackDamage.setBaseValue(attackDamage.getBaseValue() * spawnerAttribute.getDamageMultiple());
    }


    // 必须同步计算
    private static boolean isMobCountOverLimit(double radius, @NonNull Location center) {
        int mobCount = Objects.requireNonNull(center.getWorld())
                .getNearbyEntities(center, radius, radius, radius, Monster.class::isInstance)
                .size();
        // 示例:
        // 半径:12, 怪物上限~16
        // 半径:24, 怪物上限~64
        // 不考虑半径太大的情况
        return mobCount > (radius * radius / 9.0);
    }

    private static boolean isDecidedToSpawn(double spawnChance, @NonNull ThreadLocalRandom random) {
        return random.nextDouble() < spawnChance;
    }

    @Override
    public void enable() {
        startSpawnMob();
    }

    @Override
    public void disable() {
        stopSpawnMob();
    }
}
