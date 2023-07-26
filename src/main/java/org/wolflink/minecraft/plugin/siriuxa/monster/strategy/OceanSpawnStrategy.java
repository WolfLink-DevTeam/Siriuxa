package org.wolflink.minecraft.plugin.siriuxa.monster.strategy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationAPI;
import org.wolflink.minecraft.plugin.siriuxa.monster.SpawnerAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class OceanSpawnStrategy extends SpawnStrategy {

    private static final List<Biome> oceanBiomes;

    static {
        oceanBiomes = List.of(
                Biome.RIVER,
                Biome.FROZEN_RIVER,
                Biome.OCEAN,
                Biome.COLD_OCEAN,
                Biome.WARM_OCEAN,
                Biome.DEEP_OCEAN,
                Biome.FROZEN_OCEAN,
                Biome.LUKEWARM_OCEAN,
                Biome.DEEP_COLD_OCEAN,
                Biome.DEEP_FROZEN_OCEAN,
                Biome.DEEP_LUKEWARM_OCEAN);
    }

    private static final int SAFE_RADIUS = 10;
    private static final int MAX_RADIUS = 30;

    public OceanSpawnStrategy(SpawnerAttribute spawnerAttribute) {
        super(spawnerAttribute);
    }

    @Override
    public boolean isApplicable(Player player) {
        Biome biome = player.getLocation().getBlock().getBiome();
        return (oceanBiomes.contains(biome) &&
                player.getLocation().getBlock().isLiquid());
    }

    @Override
    public void spawn(Player player) {
        List<Location> locList = new ArrayList<>();
        Location firstLoc = player.getLocation();
        boolean[] available = new boolean[]{true};
        Location[] lastLoc = new Location[]{player.getLocation()};
        // 每 0.1 秒获取一次 Yaw
        int taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(Siriuxa.getInstance(), () -> {
            if (!available[0]) return;
            Location playerLoc = player.getLocation();
            // 与初始坐标不在同一世界
            if (firstLoc.getWorld() != playerLoc.getWorld()) {
                available[0] = false;
                return;
            }
            // 在0.1秒移动了长达20格
            if (lastLoc[0].distance(playerLoc) >= 20) {
                available[0] = false;
                return;
            }
            lastLoc[0] = playerLoc;
            locList.add(playerLoc);
        }, 2, 2).getTaskId();
        // 3秒后
        Bukkit.getScheduler().runTaskLaterAsynchronously(Siriuxa.getInstance(), () -> {
            Bukkit.getScheduler().cancelTask(taskId);
            if (!available[0]) {
                return; // 如果检测异常，则不生成怪物
            }
            List<Location> copyList = new ArrayList<>(locList);
            double averX = copyList.stream().map(Location::getX).reduce(0.0, Double::sum) / copyList.size();
            double averY = copyList.stream().map(Location::getY).reduce(0.0, Double::sum) / copyList.size();
            double averZ = copyList.stream().map(Location::getZ).reduce(0.0, Double::sum) / copyList.size();
            float averYaw = copyList.stream().map(Location::getYaw).reduce(0.0f, Float::sum) / copyList.size();
            Location averLocation = new Location(firstLoc.getWorld(), averX, averY, averZ, averYaw, 0);
            float minYaw = averYaw + 180 - 45;
            float maxYaw = averYaw + 180 + 45;
            ThreadLocalRandom random = ThreadLocalRandom.current();
            float randYaw = random.nextFloat(minYaw, maxYaw);
            double randDistance = random.nextDouble(SAFE_RADIUS, MAX_RADIUS);
            LocationAPI locationAPI = IOC.getBean(LocationAPI.class);
            Location goalLocation = locationAPI.getLocationByAngle(averLocation, randYaw, randDistance);
            Location summonLocation = locationAPI.getNearestSurface(goalLocation, 16);
            if (summonLocation == null) return;
            Bukkit.getScheduler().runTask(Siriuxa.getInstance(), () -> {
                World world = firstLoc.getWorld();
                assert world != null;
                if (!world.getNearbyEntities(summonLocation, 8, 4, 8, entity -> entity.getType() == EntityType.PLAYER).isEmpty())
                    return;
                EntityType entityType = getSpawnerAttribute().randomType();
                Monster monster = (Monster) world.spawnEntity(summonLocation, entityType);
                if (entityType.equals(EntityType.RABBIT)) {
                    Rabbit rabbit = (Rabbit) monster;
                    rabbit.setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
                }
                AttributeInstance maxHealth = monster.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                AttributeInstance movementSpeed = monster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                AttributeInstance attackDamage = monster.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                if (maxHealth != null) {
                    maxHealth.setBaseValue(maxHealth.getBaseValue() * getSpawnerAttribute().getHealthMultiple());
                    monster.setHealth(maxHealth.getBaseValue());
                }
                if (movementSpeed != null)
                    movementSpeed.setBaseValue(movementSpeed.getBaseValue() * getSpawnerAttribute().getMovementMultiple());
                if (attackDamage != null)
                    attackDamage.setBaseValue(attackDamage.getBaseValue() * getSpawnerAttribute().getDamageMultiple());
            });
        }, 20 * 3L);
    }
}
