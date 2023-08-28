package org.wolflink.minecraft.plugin.siriuxa.monster.strategy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.AttributeAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.world.BlockAPI;
import org.wolflink.minecraft.plugin.siriuxa.monster.SpawnerAttribute;

import java.util.Objects;
import java.util.Random;


public class OceanSpawnStrategy extends SpawnStrategy {

    private static final int SAFE_RADIUS = 8;
    private final Random random = new Random();

    public OceanSpawnStrategy(SpawnerAttribute spawnerAttribute) {
        super(spawnerAttribute);
    }

    @Override
    public boolean isApplicable(Player player) {
        return IOC.getBean(BlockAPI.class).checkIsOcean(player.getLocation());
    }

    @Override
    void spawn(Player player, final int triedCount) {
        if (triedCount <= 0) return;
        World world = player.getWorld();
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();

        int newX = x + random.nextInt(SAFE_RADIUS, SAFE_RADIUS + 10);
        int newZ = z + random.nextInt(SAFE_RADIUS, SAFE_RADIUS + 10);
        int newY = y + random.nextInt(-4, 4);
        if (player.getWorld().getBlockAt(newX, newY, newZ).getType().isSolid()) {
            spawn(player, triedCount - 1);
            return;
        }
        if (newY > player.getWorld().getHighestBlockYAt(newX, newZ))
            newY = player.getWorld().getHighestBlockYAt(newX, newZ);
        Location summonLocation = new Location(player.getWorld(), newX, newY, newZ);

        Bukkit.getScheduler().runTask(Siriuxa.getInstance(), () -> {
            if (!world.getNearbyEntities(summonLocation, 8, 4, 8, entity -> entity.getType() == EntityType.PLAYER).isEmpty()) {
                spawn(player, triedCount - 1);
                return;
            }
            EntityType entityType = random.nextDouble() < 0.85 ? EntityType.DROWNED : EntityType.GUARDIAN;
            Monster monster = (Monster) world.spawnEntity(summonLocation, entityType);
            IOC.getBean(AttributeAPI.class).multiplyAttribute(monster, "o_health",
                    Attribute.GENERIC_MAX_HEALTH, getSpawnerAttribute().getHealthMultiple());
            monster.setHealth(Objects.requireNonNull(monster.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
            IOC.getBean(AttributeAPI.class).multiplyAttribute(monster, "o_speed",
                    Attribute.GENERIC_MOVEMENT_SPEED, getSpawnerAttribute().getMovementMultiple());
            IOC.getBean(AttributeAPI.class).multiplyAttribute(monster, "o_attack",
                    Attribute.GENERIC_ATTACK_DAMAGE, getSpawnerAttribute().getDamageMultiple());
            appendMetadata(player,monster);
            callEvent(player,monster);
        });
    }
}


