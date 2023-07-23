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

    /**
     * 在一定的Y轴误差内(建议至少>=3)，
     * 获取离给定坐标最近的坐标地面(至少3格高度的空间)，不一定是地表(X和Z不改变)
     * 如果没能找到则返回null
     */
    public Location getNearestSurface(Location location,int deltaY) {
        for (int i = 1; i <= deltaY; i++) {
            Location upLoc0 = location.clone().add(0,-3 + i,0);
            Location upLoc1 = location.clone().add(0,-2 + i,0);
            Location upLoc2 = location.clone().add(0,-1 + i,0);
            Location upLoc3 = location.clone().add(0,i,0);
            if(upLoc0.getBlock().getType().isBlock()
                    && upLoc3.getBlock().getType().isAir()
                    && upLoc2.getBlock().getType().isAir()
                    && upLoc1.getBlock().getType().isAir()
            ) return upLoc1;
            Location downLoc0 = location.clone().add(0,-1-i,0);
            Location downLoc1 = location.clone().add(0,-i,0);
            Location downLoc2 = location.clone().add(0,1-i,0);
            Location downLoc3 = location.clone().add(0,2-i,0);
            if(downLoc0.getBlock().getType().isBlock()
                    && downLoc3.getBlock().getType().isAir()
                    && downLoc2.getBlock().getType().isAir()
                    && downLoc1.getBlock().getType().isAir()
            ) return downLoc1;
        }
        return null;
    }
}
