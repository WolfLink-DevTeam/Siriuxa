package org.wolflink.minecraft.plugin.siriuxa.monster.strategy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.AttributeAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationAPI;
import org.wolflink.minecraft.plugin.siriuxa.monster.SpawnerAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class PlayerFocusSpawnStrategy extends SpawnStrategy {

    public PlayerFocusSpawnStrategy(SpawnerAttribute spawnerAttribute) {
        super(spawnerAttribute);
    }

    @Override
    public boolean isApplicable(Player player) {
        // TODO 暂时不需要额外检测
        return true;
    }

    private static final int SAFE_RADIUS = 15;
    private static final int MAX_RADIUS = 35;

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
            // TODO 暂时忽略 pitch，未来可以考虑引入
            Location averLocation = new Location(firstLoc.getWorld(), averX, averY, averZ, averYaw, 0);
            float minYaw = averYaw - 45;
            float maxYaw = averYaw + 45;
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
                IOC.getBean(AttributeAPI.class).multiplyMonsterAttribute(monster, "pf_health",
                        Attribute.GENERIC_MAX_HEALTH, getSpawnerAttribute().getHealthMultiple());
                IOC.getBean(AttributeAPI.class).multiplyMonsterAttribute(monster, "pf_speed",
                        Attribute.GENERIC_MOVEMENT_SPEED, getSpawnerAttribute().getMovementMultiple());
                IOC.getBean(AttributeAPI.class).multiplyMonsterAttribute(monster, "pf_attack",
                        Attribute.GENERIC_ATTACK_DAMAGE, getSpawnerAttribute().getDamageMultiple());
            });
        }, 20 * 3L);
    }
}
