package org.wolflink.minecraft.plugin.siriuxa.api.world;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * 怪物生成盒空间
 * 一共由垂直连续的4个坐标组成
 */
public class MonsterSpawnBox {

    /**
     * 符合生成怪物的条件
     * 即：上三格不为方块，底部为方块
     */
    private static final Set<Material> availableTypes = new HashSet<>();

    static {
        availableTypes.add(Material.AIR);
        availableTypes.add(Material.CAVE_AIR);
        availableTypes.add(Material.SNOW);
        availableTypes.add(Material.SHORT_GRASS);
        availableTypes.add(Material.TALL_GRASS);
        availableTypes.add(Material.TALL_SEAGRASS);
        availableTypes.add(Material.SEAGRASS);
    }

    private final Location[] box = new Location[4];

    public MonsterSpawnBox(Location bottomLocation) {
        box[0] = bottomLocation.clone();
        box[1] = bottomLocation.clone().add(0, 1, 0);
        box[2] = bottomLocation.clone().add(0, 2, 0);
        box[3] = bottomLocation.clone().add(0, 3, 0);
    }

    /**
     * 向上移动一格
     */
    public void up() {
        for (int i = 0; i < 4; i++) {
            box[i].add(0, 1, 0);
        }
    }

    /**
     * 向下移动一格
     */
    public void down() {
        for (int i = 0; i < 4; i++) {
            box[i].add(0, -1, 0);
        }
    }

    public Location getBottom() {
        return box[0];
    }

    public boolean isAvailable() {
        Material b0 = box[0].getBlock().getType();
        Material b1 = box[1].getBlock().getType();
        Material b2 = box[2].getBlock().getType();
        Material b3 = box[3].getBlock().getType();
        return box[0].getBlock().getType().isSolid()
                && availableTypes.contains(b1)
                && availableTypes.contains(b2)
                && availableTypes.contains(b3);
    }
}
