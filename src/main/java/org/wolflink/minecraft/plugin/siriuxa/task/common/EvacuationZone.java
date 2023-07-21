package org.wolflink.minecraft.plugin.siriuxa.task.common;

import com.sk89q.worldedit.EditSession;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationCommandSender;
import org.wolflink.minecraft.plugin.siriuxa.api.world.WorldEditAPI;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import java.util.HashSet;
import java.util.List;
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

    public EvacuationZone(Task task, World world, int x, int z, int safeRadius) {
        this.task = task;
        this.center = new Location(world, x, world.getHighestBlockYAt(x, z) + 25, z);
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
     */
    public Set<Player> getPlayerInZone() {
        Set<Player> playerSet = new HashSet<>();
        if (!available || center.getWorld() == null) return playerSet;
        for (Entity p : center.getWorld().getNearbyEntities(center, safeRadius, safeRadius, safeRadius,
                entity -> entity.getType().equals(EntityType.PLAYER))) {
            // 玩家脚下是末地门
            if (p.getLocation().clone().getBlock().getType().equals(Material.END_PORTAL_FRAME)) {
                playerSet.add((Player) p);
            }
        }
        return playerSet;
    }

    private EditSession editSession;

    public void generateSchematic() {
        editSession = IOC.getBean(WorldEditAPI.class).pasteEvacuationUnit(locationCommandSender);
        setPlayerCompass(task.getPlayers(), available);
        Notifier.broadcastChat(task.getPlayers(), "飞艇已停留至坐标 X：" + center.getBlockX() + " Z：" + center.getBlockZ() + " 附近，如有需要请尽快前往撤离。");
    }

    public void undoSchematic() {
        Notifier.broadcastChat(task.getPlayers(), "坐标 X：" + center.getBlockX() + " Z：" + center.getBlockZ() + " 附近的飞艇已撤离，请等待下一艘飞艇接应。");
        setPlayerCompass(task.getPlayers(), available);
        IOC.getBean(WorldEditAPI.class).undoPaste(locationCommandSender, editSession);
    }

    public void setPlayerCompass(List<Player> playerList, boolean available) {
        ItemMeta compassMeta = new ItemStack(Material.COMPASS).getItemMeta();
        if (compassMeta == null) {
            Notifier.warn("获取撤离指南针的itemMeta失败");
            return;
        }
        compassMeta.setDisplayName("撤离指南针");
        compassMeta.setLore(List.of("指向最近的撤离点"));

        for (Player player : playerList) {
            Inventory inv = player.getInventory();
            for (ItemStack item : inv) {
                if (item != null && item.getType() == Material.COMPASS) {
                    if (available) {
                        item.setItemMeta(compassMeta);
                        player.setCompassTarget(center);
                    } else {
                        item.setItemMeta(new ItemStack(Material.COMPASS).getItemMeta());
                        // 虽然task.getTaskRegion()可能返回null，但我坚信米可不会让这个发生
                        player.setCompassTarget(task.getTaskRegion().getCenter());
                    }
                }
            }
        }
    }
}
