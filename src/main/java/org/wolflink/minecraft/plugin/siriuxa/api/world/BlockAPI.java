package org.wolflink.minecraft.plugin.siriuxa.api.world;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.wolflink.common.ioc.Singleton;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class BlockAPI {
    /**
     * 半径为radius的方形区域内搜索方块
     */
    @NonNull
    public List<Location> searchBlock(Material material, Location center, int radius) {
        World world = center.getWorld();
        List<Location> result = new ArrayList<>();
        if (world == null) return result;
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (world.getBlockAt(centerX + x, centerY + y, centerZ + z).getType().equals(material)) {
                        result.add(center.clone().add(x, y, z));
                    }
                }
            }
        }
        return result;
    }

    private static final int OCEAN_RADIUS = 8;

    /**
     * 检查给定坐标是否处于海洋中
     */
    public boolean checkIsOcean(Location center) {
        if (center.getBlock().getType() != Material.WATER) return false;
        World world = center.getWorld();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();
        for (int x = -OCEAN_RADIUS; x <= OCEAN_RADIUS; x++) {
            for (int z = -OCEAN_RADIUS; z <= OCEAN_RADIUS; z++) {
                Location tempLoc = new Location(world, centerX + (double) x, centerY, centerZ + (double) z);
                if (tempLoc.getBlock().getType() != Material.WATER) {
                    return true;
                }
            }
        }
        return false;
    }
}
