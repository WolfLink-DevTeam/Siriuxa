package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitBlockCommandSender;
import com.sk89q.worldedit.bukkit.BukkitPlayerBlockBag;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.EvacuationCenter;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.WorldEditAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class EvacuationZone {

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
        center.setY(world.getHighestBlockYAt(center.getBlockX(),center.getBlockZ()));
        this.safeRadius = safeRadius;
        evacuationCenter = new EvacuationCenter(center);
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
    private final EvacuationCenter evacuationCenter;
    private final LocalSession localSession = new LocalSession(WorldEdit.getInstance().getConfiguration());
    private EditSession editSession;
    /**
     * TODO 生成结构
     */
    public void generateSchematic() {
        localSession.setWorldOverride(BukkitAdapter.adapt(center.getWorld()));
        Notifier.debug("飞艇结构已生成");
        editSession = IOC.getBean(WorldEditAPI.class).pasteEvacuationUnit(center);
        localSession.remember(editSession);
//        editSession.commit();
//        center.getBlock().setType(Material.BEACON);
    }

    /**
     * TODO 撤销已生成的结构
     */
    public void undoSchematic() {
        localSession.undo(null,editSession.getActor());
//        editSession.close();
        Notifier.debug("飞艇结构已撤回");
    }
}
