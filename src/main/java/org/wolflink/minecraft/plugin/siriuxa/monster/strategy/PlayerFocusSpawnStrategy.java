package org.wolflink.minecraft.plugin.siriuxa.monster.strategy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


@Singleton
public class PlayerFocusSpawnStrategy extends SpawnStrategy{

    @Override
    public boolean isApplicable(Player player) {
        // TODO 暂时不需要额外检测
        return true;
    }

    private final int SAFE_RADIUS = 10;
    private final int MAX_RADIUS = 30;

    @Override
    public void spawn(Player player) {
        List<Location> locList = new ArrayList<>();
        Location firstLoc = player.getLocation();
        boolean[] available = new boolean[]{true};
        // 每 0.1 秒获取一次 Yaw
        int taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(Siriuxa.getInstance(),()->{
            // 与初始坐标不在同一世界
            if(firstLoc.getWorld() != player.getLocation().getWorld()) {
                available[0] = false;
                return;
            }
            // 在0.1秒移动了长达20格
            if(firstLoc.distance(player.getLocation()) >= 20){
                available[0] = false;
                return;
            }
            locList.add(player.getLocation());
        },2,2).getTaskId();
        // 3秒后
        Bukkit.getScheduler().runTaskLaterAsynchronously(Siriuxa.getInstance(), ()-> {
            Bukkit.getScheduler().cancelTask(taskId);
            if(!available[0]) return; // 如果检测异常，则不生成怪物
            double averX = locList.stream().map(Location::getX).reduce(0.0,Double::sum);
            double averY = locList.stream().map(Location::getY).reduce(0.0,Double::sum);
            double averZ = locList.stream().map(Location::getZ).reduce(0.0,Double::sum);
            float averYaw = locList.stream().map(Location::getYaw).reduce(0.0f,Float::sum);
            // TODO 暂时忽略 pitch，未来可以考虑引入
            Location averLocation = new Location(firstLoc.getWorld(), averX,averY,averZ,averYaw,0);
            float minYaw = averYaw - 45;
            float maxYaw = averYaw + 45;
            ThreadLocalRandom random = ThreadLocalRandom.current();
            float randYaw = random.nextFloat(minYaw,maxYaw);
            double randDistance = random.nextDouble(SAFE_RADIUS,MAX_RADIUS);
            LocationAPI locationAPI = IOC.getBean(LocationAPI.class);
            Location goalLocation = locationAPI.getLocationByAngle(averLocation,randYaw,randDistance);
            Location summonLocation = locationAPI.getNearestSurface(goalLocation,5);
            if(summonLocation == null) return;
            // TODO 测试怪物(小黑)
            Objects.requireNonNull(firstLoc.getWorld()).spawnEntity(summonLocation, EntityType.ENDERMAN);
        },20 * 3);
    }
}
