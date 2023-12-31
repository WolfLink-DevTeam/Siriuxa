package org.wolflink.minecraft.plugin.siriuxa.api.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;

@Singleton
public class RegionAPI {
    @Inject
    Config config;

    public Location autoGetRegionCenter(World world) {
        return autoGetRegionCenter(world, 5);
    }

    /**
     * 根据区块索引指针划分区块区域
     *
     * @param world 指定世界
     * @param retry 重试次数
     */
    private Location autoGetRegionCenter(World world, int retry) {
        int index = config.getNextRegionIndex();
        int spacingRadius = config.get(ConfigProjection.EXPLORATION_REGION_SPACING_RADIUS);
        double radius = config.get(ConfigProjection.EXPLORATION_REGION_RADIUS);
        int maxLength = config.get(ConfigProjection.EXPLORATION_REGION_TOTAL_LENGTH);
        int areaLength = (int) ((spacingRadius + radius) * 2);
        int maxRegionPerLine = 2 * maxLength / areaLength;
        int x = index % maxRegionPerLine;
        int z = (index / maxRegionPerLine) % maxRegionPerLine;
        int centerX = -maxLength + x * areaLength;
        int centerZ = -maxLength + z * areaLength;
        int y = world.getHighestBlockYAt(centerX, centerZ);
        Location location = new Location(world, centerX, y, centerZ);
        if (retry > 0 && IOC.getBean(BlockAPI.class).checkIsOcean(location)) {
            Notifier.debug("检测到海洋：X " + location.getBlockX() + " Z " + location.getBlockZ());
            return autoGetRegionCenter(world, retry - 1);
        }
        return location;
    }
}
