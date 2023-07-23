package org.wolflink.minecraft.plugin.siriuxa.api.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.wolflink.common.ioc.Singleton;

@Singleton
public class LocationAPI {
    /**
     * 获取以给定坐标为中心，水平角度为 yaw，距离为 distance 的目标坐标
     */
    public Location getLocationByAngle(Location center, double yaw, double distance) {
        double angle = (yaw - 90) % 360;
        if (angle < 0) {
            angle += 360;
        }
        // 将角度转换为弧度
        double radian = Math.toRadians(angle);
        // 计算新坐标的X和Z值
        double deltaX = Math.sin(radian) * distance;
        double deltaZ = Math.cos(radian) * distance;

        // 获取中心坐标的世界、X、Y、Z值
        World world = center.getWorld();
        double centerX = center.getX();
        double centerY = center.getY();
        double centerZ = center.getZ();

        // 计算新坐标的X、Y、Z值
        double newX = centerX + deltaX;
        double newY = centerY;  // 我们假设Y值（高度）不变
        double newZ = centerZ + deltaZ;

        // 创建新的Location对象
        Location newLocation = new Location(world, newX, newY, newZ);

        return newLocation;
    }
}
