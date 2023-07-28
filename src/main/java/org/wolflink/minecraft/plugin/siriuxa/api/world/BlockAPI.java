package org.wolflink.minecraft.plugin.siriuxa.api.world;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class BlockAPI {
    private static final int OCEAN_RADIUS = 5;
    private static final int MAX_WATER_LEVEL_Y = 96;
    /**
     * 处在水中的方块材质
     */
    private static final Set<Material> waterMaterials = new HashSet<>();

    static {
        waterMaterials.add(Material.WATER);
        waterMaterials.add(Material.SEAGRASS);
        waterMaterials.add(Material.TALL_SEAGRASS);
        waterMaterials.add(Material.KELP);
        waterMaterials.add(Material.BRAIN_CORAL_BLOCK);
        waterMaterials.add(Material.BUBBLE_CORAL_BLOCK);
        waterMaterials.add(Material.FIRE_CORAL_BLOCK);
        waterMaterials.add(Material.TUBE_CORAL_BLOCK);
        waterMaterials.add(Material.HORN_CORAL_BLOCK);
    }

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

    /**
     * 检查给定坐标是否处于海洋中
     */
    public boolean checkIsOcean(Location center) {
        if (center.getBlock().getType() != Material.WATER) return false;
        World world = center.getWorld();
        int centerX = center.getBlockX();
        int centerY = getWaterLevelY(center);
        Notifier.debug("水平面：" + centerY);
        if (centerY == -1) {
            Notifier.debug("未找到坐标所处的水平面：" + center.getBlockX() + "|" + center.getBlockY() + "|" + center.getBlockZ());
            return false;
        }
        int centerZ = center.getBlockZ();
        for (int x = -OCEAN_RADIUS; x <= OCEAN_RADIUS; x++) {
            for (int z = -OCEAN_RADIUS; z <= OCEAN_RADIUS; z++) {
                Location tempLoc = new Location(world, centerX + x, centerY, centerZ + z);
                if (tempLoc.getBlock().getType() != Material.WATER) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取给定坐标当前所处水域的水平面
     * -1 则为没有找到
     */
    public int getWaterLevelY(Location waterLocation) {
        Block block = waterLocation.getBlock();
        while (block.getY() < MAX_WATER_LEVEL_Y) {
            Block upBlock = block.getRelative(0, 1, 0);
            if (!waterMaterials.contains(upBlock.getType())) return block.getY();
            else block = upBlock;
        }
        return -1;
    }
}
