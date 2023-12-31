package org.wolflink.minecraft.plugin.siriuxa.api.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.wolflink.common.ioc.Singleton;

import javax.annotation.Nullable;

@Singleton
public class LocationAPI {
    /**
     * 获取以给定坐标为中心，水平角度为 yaw，距离为 distance 的目标坐标
     */
    public Location getLocationByAngle(Location center, double yaw, double distance) {
        double angle = (yaw + 90) % 360;
        if (angle < 0) {
            angle += 360;
        }
        // 将角度转换为弧度
        double radian = Math.toRadians(angle);
        // 计算新坐标的X和Z值
        double deltaX = Math.cos(radian) * distance;
        double deltaZ = Math.sin(radian) * distance;

        // 获取中心坐标的世界、X、Y、Z值
        World world = center.getWorld();
        double centerX = center.getX();
        double centerY = center.getY();
        double centerZ = center.getZ();

        // 计算新坐标的X、Y、Z值
        double newX = centerX + deltaX;
        double newY = centerY;
        double newZ = centerZ + deltaZ;

        // 创建新的Location对象
        return new Location(world, newX, newY, newZ);
    }

    /**
     * 在一定的Y轴误差内(建议至少>=3)，
     * 获取离给定坐标最近的坐标地面(至少3格高度的空间)，不一定是地表(X和Z不改变)
     * 如果没能找到则返回null
     */
    @Nullable
    public Location getNearestSurface(Location location, int deltaY) {
        MonsterSpawnBox upBox = new MonsterSpawnBox(location.clone().add(0, -1, 0));
        MonsterSpawnBox downBox = new MonsterSpawnBox(location.clone().add(0, -3, 0));
        for (int i = 0; i < deltaY; i++) {
            if (upBox.isAvailable()) return upBox.getBottom().add(0, 1, 0);
            if (downBox.isAvailable()) return downBox.getBottom().add(0, 1, 0);
            upBox.up();
            downBox.down();
        }
        return null;
    }

    /**
     * 在一定的Y轴误差内获取给定坐标最近的固体方块坐标(不包含自身)
     */
    @Nullable
    public Location getNearestSolid(Location location, int deltaY) {
        Block center = location.getBlock();
        for (int i = 1; i <= deltaY; i++) {
            if (center.getRelative(0, i, 0).getType().isSolid()) return location.clone().add(0, i, 0);
            if (center.getRelative(0, -i, 0).getType().isSolid()) return location.clone().add(0, -i, 0);
        }
        return null;
    }
}
