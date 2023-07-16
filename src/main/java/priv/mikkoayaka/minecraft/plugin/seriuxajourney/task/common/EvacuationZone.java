package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common;

import com.sk89q.worldedit.EditSession;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.world.LocationCommandSender;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.world.WorldEditAPI;

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
    public EvacuationZone(Location center, int safeRadius) {
        this.center = center;
        World world = center.getWorld();
        if(world == null) throw new IllegalArgumentException("安全区坐标的世界为空");
        this.safeRadius = safeRadius;
        this.locationCommandSender = new LocationCommandSender(center);
    }
    public void setAvailable(boolean value) {
        if(available == value)return;
        available = value;
        if(available) {
            generateSchematic();
        } else {
            undoSchematic();
        }
    }

    /**
     * 获取在当前撤离区域的玩家
     */
    public Set<Player> getPlayerInZone() {
        Set<Player> playerSet = new HashSet<>();
        if(!available)return playerSet;
        for(Entity p : Objects.requireNonNull(center.getWorld())
                .getNearbyEntities(center,safeRadius,safeRadius,safeRadius,
                        entity -> entity.getType().equals(EntityType.PLAYER))) {
            playerSet.add((Player) p);
        }
        return playerSet;
    }
    private EditSession editSession;
    /**
     * TODO 生成结构
     */
    public void generateSchematic() {
        editSession = IOC.getBean(WorldEditAPI.class).pasteEvacuationUnit(locationCommandSender);
    }

    /**
     * TODO 撤销已生成的结构
     */
    public void undoSchematic() {
        IOC.getBean(WorldEditAPI.class).undoPaste(locationCommandSender,editSession);
    }
}
