package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EvacuationZone {

    /**
     * 撤离的安全区域中心
     */
    private final Location center;
    /**
     * 撤离的安全区域半径
     */
    private final int safeRadius;
    public EvacuationZone(Location center, int safeRadius) {
        this.center = center;
        this.safeRadius = safeRadius;
    }

    /**
     * 获取在当前撤离区域的玩家
     */
    public Set<Player> getPlayerInZone() {
        Set<Player> playerSet = new HashSet<>();
        for(Entity p : Objects.requireNonNull(center.getWorld())
                .getNearbyEntities(center,safeRadius,safeRadius,safeRadius,
                        entity -> entity.getType().equals(EntityType.PLAYER))) {
            playerSet.add((Player) p);
        }
        return playerSet;
    }

    /**
     * 生成结构
     */
    public void generateSchematic() {
        center.getBlock().setType(Material.BEACON);
    }
}
