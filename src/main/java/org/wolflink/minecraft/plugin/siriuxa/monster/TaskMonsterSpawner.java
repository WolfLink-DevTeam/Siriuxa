package org.wolflink.minecraft.plugin.siriuxa.monster;

import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import java.util.*;

/**
 * 任务怪物生成器
 * 主要负责处理与任务相关的怪物生成
 */
public class TaskMonsterSpawner {

    @NonNull
    private final Task task;
    private final Random random = new Random(114514);

    private boolean enabled = false;

    private final SpawnerAttribute spawnerAttribute;

    private int spawnTaskId = -1;

    public void setEnabled(boolean value) {
        if(enabled == value)return;
        enabled = value;
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(),()->{
            if(enabled) startSpawnMob();
            else stopSpawnMob();
        });
    }

    public TaskMonsterSpawner(@NonNull Task task) {
        this.task = task;
        spawnerAttribute = new SpawnerAttribute(task.getTaskDifficulty());
    }
    private final double MIN_RADIUS = 10.0;
    private final double MAX_RADIUS = 20.0;
    private void startSpawnMob() {
        if(spawnTaskId == -1) {
            spawnTaskId = spawnMobTask(MIN_RADIUS,MAX_RADIUS).getTaskId();
        }
    }
    private void stopSpawnMob() {
        if(spawnTaskId != -1) {
            Bukkit.getScheduler().cancelTask(spawnTaskId);
            spawnTaskId = -1;
        }
    }

    private @NonNull BukkitTask spawnMobTask(double minRadius, double maxRadius) {
        Plugin plugin = Siriuxa.getInstance();
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            task.getPlayers().forEach(p -> spawnMobAroundPlayer(minRadius, maxRadius, p));
        }, 20 * 15, 20 * 15);
    }

    /**
     * 在玩家周围生成一只怪物
     */
    private void spawnMobAroundPlayer(double minRadius, double maxRadius,Player player) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        assert world != null;
        if (isMobCountOverLimit(maxRadius, loc)) return;
        double r = maxRadius - minRadius;
        double x = loc.getX() + Math.random() * r * 2 - r;
        double z = loc.getZ() + Math.random() * r * 2 - r;
        double y = world.getHighestBlockYAt((int) x, (int) z);
        Location spawnLoc = new Location(world, x, y, z);
        if (spawnLoc.getBlock().isLiquid()) return;

        // customize the mob type, health, movement speed and damage
        int temp = random.nextInt(100);
        EntityType entityType;
        if (temp < 20) return; // 20% chance to not spawn any mob
        else if (temp < 60) entityType = EntityType.ZOMBIE; // 40%
        else if (temp < 80) entityType = EntityType.SKELETON; // 20%
        else if (temp < 90) entityType = EntityType.SPIDER; // 10%
        else if (temp < 99) entityType = EntityType.CREEPER; // 9%
        else entityType = EntityType.WARDEN; // 1%
        Bukkit.getScheduler().runTask(Siriuxa.getInstance(),()->{
            Monster monster = (Monster) world.spawnEntity(spawnLoc, entityType);
            AttributeInstance maxHealth = monster.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            AttributeInstance movementSpeed = monster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            AttributeInstance attackDamage = monster.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
            if (maxHealth != null) {
                maxHealth.setBaseValue(maxHealth.getBaseValue() * spawnerAttribute.getHealthMultiple());
                monster.setHealth(maxHealth.getBaseValue());
            }
            if (movementSpeed != null) movementSpeed.setBaseValue(movementSpeed.getBaseValue() * spawnerAttribute.getMovementMultiple());
            if (attackDamage != null) attackDamage.setBaseValue(attackDamage.getBaseValue() * spawnerAttribute.getDamageMultiple());
        });
    }

    private static boolean isMobCountOverLimit(double radius, @NonNull Location center) {
        int mobCount = Objects.requireNonNull(center.getWorld())
                .getNearbyEntities(center,radius,radius,radius, entity -> entity instanceof Monster)
                .size();
        // 示例:
        // 半径:15, 怪物上限~50
        // 半径:20, 怪物上限~80
        // 半径:25, 怪物上限~120
        // 不考虑半径太大的情况
        return mobCount > 15 + (radius * radius / 6.0);
    }
}
