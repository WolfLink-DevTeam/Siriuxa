package org.wolflink.minecraft.plugin.siriuxa.monster.strategy;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.AttributeAPI;
import org.wolflink.minecraft.plugin.siriuxa.monster.SpawnerAttribute;

import java.util.List;
import java.util.Objects;
import java.util.Random;


public class OceanSpawnStrategy extends SpawnStrategy {

    private final Random random = new Random();
    private static final List<Biome> oceanBiomes;

    static {
        oceanBiomes = List.of(
                Biome.LUSH_CAVES,
                Biome.SWAMP,
                Biome.MANGROVE_SWAMP,
                Biome.BEACH,
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

    private static final int SAFE_RADIUS = 8;

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
        World world = player.getWorld();
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();

        int newX = x + random.nextInt(SAFE_RADIUS, SAFE_RADIUS + 10);
        int newZ = z + random.nextInt(SAFE_RADIUS, SAFE_RADIUS + 10);
        int newY = y + random.nextInt(-4, 4);
        if (player.getWorld().getBlockAt(newX, newY, newZ).getType().isSolid()) return;
        if (newY > player.getWorld().getHighestBlockYAt(newX, newZ))
            newY = player.getWorld().getHighestBlockYAt(newX, newZ);
        Location summonLocation = new Location(player.getWorld(), newX, newY, newZ);

        if (!world.getNearbyEntities(summonLocation, 8, 4, 8, entity -> entity.getType() == EntityType.PLAYER).isEmpty())
            return;
        EntityType entityType = random.nextDouble() < 0.75 ? EntityType.DROWNED : EntityType.GUARDIAN;
        Monster monster = (Monster) world.spawnEntity(summonLocation, entityType);
        IOC.getBean(AttributeAPI.class).multiplyMonsterAttribute(monster, "o_health",
                Attribute.GENERIC_MAX_HEALTH, getSpawnerAttribute().getHealthMultiple());
        monster.setHealth(Objects.requireNonNull(monster.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        IOC.getBean(AttributeAPI.class).multiplyMonsterAttribute(monster, "o_speed",
                Attribute.GENERIC_MOVEMENT_SPEED, getSpawnerAttribute().getMovementMultiple());
        IOC.getBean(AttributeAPI.class).multiplyMonsterAttribute(monster, "o_attack",
                Attribute.GENERIC_ATTACK_DAMAGE, getSpawnerAttribute().getDamageMultiple());
    }
}


