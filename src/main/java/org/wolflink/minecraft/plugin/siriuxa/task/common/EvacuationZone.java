package org.wolflink.minecraft.plugin.siriuxa.task.common;

import com.sk89q.worldedit.EditSession;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationCommandSender;
import org.wolflink.minecraft.plugin.siriuxa.api.world.WorldEditAPI;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EvacuationZone {

    private final LocationCommandSender locationCommandSender;
    /**
     * 撤离的安全区域中心
     */
    @Getter
    private final Location center;

    /**
     * 是否可用
     */
    private boolean available = false;
    /**
     * 撤离的安全区域半径
     */
    private final int safeRadius;

    /**
     * 归属的任务
     */
    private final Task task;

    public EvacuationZone(Task task,World world,int x,int z, int safeRadius) {
        this.task = task;
        this.center = new Location(world,x,world.getHighestBlockYAt(x,z)+25,z);
        this.safeRadius = safeRadius;
        this.locationCommandSender = new LocationCommandSender(center);
    }

    public void setAvailable(boolean value) {
        if (available == value) return;
        available = value;
        if (available) {
            generateSchematic();
        } else {
            undoSchematic();
        }
    }

    /**
     * 获取在当前撤离区域的玩家
     * TODO 撤离判断
     */
    public Set<Player> getPlayerInZone() {
        Set<Player> playerSet = new HashSet<>();
        if (!available) return playerSet;
        for (Entity p : Objects.requireNonNull(center.getWorld())
                .getNearbyEntities(center, safeRadius, safeRadius, safeRadius,
                        entity -> entity.getType().equals(EntityType.PLAYER))) {
            playerSet.add((Player) p);
        }
        return playerSet;
    }

    private EditSession editSession;

    public void generateSchematic() {
        editSession = IOC.getBean(WorldEditAPI.class).pasteEvacuationUnit(locationCommandSender);
        Notifier.broadcastChat(task.getPlayers(), "飞艇已停留至坐标 X：" + center.getBlockX() + " Z：" + center.getBlockZ() + " 附近，如有需要请尽快前往撤离。");
    }

    public void undoSchematic() {
        Notifier.broadcastChat(task.getPlayers(), "坐标 X：" + center.getBlockX() + " Z：" + center.getBlockZ() + " 附近的飞艇已撤离，请等待下一艘飞艇接应。");
        IOC.getBean(WorldEditAPI.class).undoPaste(locationCommandSender, editSession);
    }
}
