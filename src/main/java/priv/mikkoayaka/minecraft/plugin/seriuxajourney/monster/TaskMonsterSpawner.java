package priv.mikkoayaka.minecraft.plugin.seriuxajourney.monster;

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
import org.jetbrains.annotations.NotNull;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;

import java.util.List;
import java.util.Random;

public class TaskMonsterSpawner {

    @NonNull
    final Task task;
    final int taskLevel;
    private final Random random = new Random(114514);

    @Setter
    private boolean onSpawn = true;

    public TaskMonsterSpawner(@NotNull Task task) {
        this.task = task;
        this.taskLevel = task.getTaskDifficulty().getLevel();
        switch (this.taskLevel) {
            case 1 -> { // 轻松
                healthMultiple = 0.6;
                movementMultiple = 0.8;
                damageMultiple = 0.6;
            }
            case 2 -> { // 常规
                healthMultiple = 1.0;
                movementMultiple = 1.0;
                damageMultiple = 1.0;
            }
            case 3 -> { // 困难
                healthMultiple = 1.5;
                movementMultiple = 1.2;
                damageMultiple = 1.5;
            }
            case 4 -> { // 专家
                healthMultiple = 2.0;
                movementMultiple = 1.5;
                damageMultiple = 2.0;
            }
            default -> throw new IllegalArgumentException("Invalid task level.");
        }
    }

    @Setter
    private double healthMultiple = 0;
    @Setter
    private double movementMultiple = 0;
    @Setter
    private double damageMultiple = 0;

    public void spawnMob(double minRadius, double maxRadius, Location @NotNull ... locations) {
        if (maxRadius - minRadius < 1.0 || minRadius < 1.0)
            throw new IllegalArgumentException("Invalid minRadius or maxRadius value.");
        List<BukkitTask> spawnTasks = null;
        for (Location loc : locations) {
            if (onSpawn) spawnTasks.add(spawnMobTask(minRadius, maxRadius, loc));
        }
    }

    private @NotNull BukkitTask spawnMobTask(double minRadius, double maxRadius, Location loc) {
        Plugin plugin = SeriuxaJourney.getInstance();
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            World world = loc.getWorld();
            if (world == null) return;
            List<EntityType> nearbyEntityTypes = world.getNearbyEntities(loc, maxRadius, maxRadius, maxRadius).stream()
                    .filter(entity -> entity instanceof Monster || entity instanceof Player).map(Entity::getType).toList();
            spawnMobAroundPlayer(minRadius, maxRadius, loc, world, nearbyEntityTypes);
        }, 20L, 20 * 5L);
    }

    private void spawnMobAroundPlayer(double minRadius, double maxRadius, Location loc, World world, @NotNull List<EntityType> nearbyEntityTypes) {
        if (!nearbyEntityTypes.contains(EntityType.PLAYER)) return;
        if (isMobCountOverLimit(maxRadius, nearbyEntityTypes)) return;
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
        Monster monster = (Monster) world.spawnEntity(spawnLoc, entityType);
        AttributeInstance maxHealth = monster.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance movementSpeed = monster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        AttributeInstance attackDamage = monster.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (maxHealth != null) maxHealth.setBaseValue(maxHealth.getBaseValue() * healthMultiple);
        if (movementSpeed != null) movementSpeed.setBaseValue(movementSpeed.getBaseValue() * movementMultiple);
        if (attackDamage != null) attackDamage.setBaseValue(attackDamage.getBaseValue() * damageMultiple);
    }

    @Contract(pure = true)
    private static boolean isMobCountOverLimit(double maxRadius, @NotNull List<EntityType> nearbyEntityTypes) {
        int mobCount = 0;
        for (EntityType entityType : nearbyEntityTypes) {
            if (entityType == EntityType.PLAYER) continue;
            mobCount++;
        }
        // example:
        // maxRadius:10, mobCount:25
        // maxRadius:20, mobCount:100
        return mobCount > (int) (maxRadius * maxRadius / 4.0);
    }
}
