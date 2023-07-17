package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.region;

import org.bukkit.Location;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Config;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.ConfigProjection;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import java.util.Objects;

public class SquareRegion extends TaskRegion {

    private final String worldName;
    private final int centerX;
    private final int centerZ;

    public SquareRegion(Task task, Location center) {
        super(task, center, IOC.getBean(Config.class).get(ConfigProjection.EXPLORATION_REGION_RADIUS));
        this.worldName = Objects.requireNonNull(center.getWorld()).getName();
        this.centerX = center.getBlockX();
        this.centerZ = center.getBlockZ();
    }

    @Override
    public double distanceToNearestBorder(Location location) {
        if (!Objects.requireNonNull(location.getWorld()).getName().equals(worldName)) {
            Notifier.warn("某玩家当前所处" + location.getWorld().getName() + "世界，但任务应该在" + worldName + "世界进行");
            return 0;
        }
        double distanceX = radius - Math.abs(centerX - location.getX());
        double distanceZ = radius - Math.abs(centerZ - location.getZ());
        return Math.min(distanceX, distanceZ);
    }
}
