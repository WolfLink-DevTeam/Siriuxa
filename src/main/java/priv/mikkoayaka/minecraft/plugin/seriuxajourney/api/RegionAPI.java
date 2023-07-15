package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Config;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.ConfigProjection;

@Singleton
public class RegionAPI {
    @Inject
    Config config;
    /**
     * 根据区块索引指针划分区块区域
     */
    public Location autoGetRegionCenter(World world) {
        int index = config.getNextRegionIndex();
        int spacingRadius = config.get(ConfigProjection.EXPLORATION_REGION_SPACING_RADIUS);
        double radius = config.get(ConfigProjection.EXPLORATION_REGION_RADIUS);
        int maxLength = config.get(ConfigProjection.EXPLORATION_REGION_TOTAL_LENGTH);
        int finalRadius = (int) ((spacingRadius + radius) * 2);
        int maxRegionPerLine = maxLength / finalRadius;
        int x = index % maxRegionPerLine;
        int z = (index / maxRegionPerLine) % maxRegionPerLine;
        int centerX = -maxLength + x * finalRadius;
        int centerZ = -maxLength + z * finalRadius;
        int y = world.getHighestBlockYAt(centerX,centerZ);
        return new Location(world,centerX,y,centerZ);
    }
}
