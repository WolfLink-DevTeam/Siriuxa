package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.region;

import org.bukkit.Location;
import org.bukkit.World;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import java.util.Objects;

public class SquareRegion extends TaskRegion{

    private final String worldName;
    private final int centerX;
    private final int centerZ;

    public SquareRegion(Task task, World world, int centerX, int centerZ, double radius) {
        super(task,world,centerX,centerZ, radius);
        this.worldName = world.getName();
        this.centerX = centerX;
        this.centerZ = centerZ;
    }

    @Override
    public double distanceToNearestBorder(Location location) {
        if(!Objects.requireNonNull(location.getWorld()).getName().equals(worldName)) {
            Notifier.warn("某玩家当前所处"+location.getWorld().getName()+"世界，但任务应该在"+worldName+"世界进行");
            return 0;
        }
        double distanceX = radius - Math.abs(centerX - location.getX());
        double distanceZ = radius - Math.abs(centerZ - location.getZ());
        return Math.min(distanceX,distanceZ);
    }
}
