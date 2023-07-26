package org.wolflink.minecraft.plugin.siriuxa.api.world;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * 幽匿结构生成盒模型
 * 中心是催发体，边上4个是幽匿块，Y轴等高
 */
public class SculkSpawnBox {

    /**
     * 0 中
     * 1 Z+1
     * 2 Z-1
     * 3 X-1
     * 4 X+1
     */
    Location[] locations = new Location[5];

    public SculkSpawnBox(Location center) {
        locations[0] = center.clone();
        locations[1] = center.clone().add(0, 0, 1);
        locations[2] = center.clone().add(0, 0, -1);
        locations[3] = center.clone().add(-1, 0, 0);
        locations[4] = center.clone().add(1, 0, 0);
    }

    public void spawn() {
        locations[0].getBlock().setType(Material.SCULK_CATALYST);
        locations[1].getBlock().setType(Material.SCULK);
        locations[2].getBlock().setType(Material.SCULK);
        locations[3].getBlock().setType(Material.SCULK);
        locations[4].getBlock().setType(Material.SCULK);
    }

    /**
     * 向下移动一格
     */
    public void down() {
        for (int i = 0; i <= 4; i++)
            locations[i].add(0, -1, 0);
    }

    /**
     * 只生成在固体中
     */
    public boolean isAvailable() {
        for (int i = 0; i <= 5; i++) {
            for (Location location : locations) {
                if (!(location.getBlock().getType().isSolid())) {
                    if (i < 5) {
                        down();
                        break;
                    } else return false;
                }
            }
        }
        return true;
    }
}
