package org.wolflink.minecraft.plugin.siriuxa.task.common;

import com.sk89q.worldedit.EditSession;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationCommandSender;
import org.wolflink.minecraft.plugin.siriuxa.api.world.WorldEditAPI;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import java.util.HashSet;
import java.util.List;
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

    public EvacuationZone(Task task, World world, int x, int z, int safeRadius) {
        this.task = task;
        this.center = new Location(world, x, world.getHighestBlockYAt(x, z) + 25, z);
        this.safeRadius = safeRadius;
        this.locationCommandSender = new LocationCommandSender(center);
    }

    public void setAvailable(boolean value) {
        if (available == value) return;
        available = value;
        setPlayerCompass(task.getPlayers(), available);
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

    // TODO 目前没有考虑暂时离线的玩家的情况
    public void generateSchematic() {
        editSession = IOC.getBean(WorldEditAPI.class).pasteEvacuationUnit(locationCommandSender);
        Notifier.broadcastChat(task.getPlayers(), "飞艇已停留至坐标 X：" + center.getBlockX() + " Z：" + center.getBlockZ() + " 附近，如有需要请尽快前往撤离。");
    }

    public void undoSchematic() {
        Notifier.broadcastChat(task.getPlayers(), "坐标 X：" + center.getBlockX() + " Z：" + center.getBlockZ() + " 附近的飞艇已撤离，请等待下一艘飞艇接应。");
        IOC.getBean(WorldEditAPI.class).undoPaste(locationCommandSender, editSession);
    }

    public void setPlayerCompass(List<Player> playerList, boolean available) {
        Notifier.broadcastChat(task.getPlayers(), "温馨提示：提前在物品栏准备好指南针，为你的撤离之旅雪中送炭。=w=");

        CompassMeta compassMeta = (CompassMeta) new ItemStack(Material.COMPASS).getItemMeta();
        if (compassMeta == null) {
            Notifier.warn("获取撤离指南针的itemMeta失败");
            return;
        }
        if (available) {
            compassMeta.setDisplayName("§a飞艇指南针");
            compassMeta.setLore(List.of("§f ", "  §7接收到了神奇的信号，指向最近的撤离飞艇", "§f "));
            compassMeta.setLodestone(center);
        } else compassMeta.setLodestone(Objects.requireNonNull(task.getTaskRegion()).getCenter());
        compassMeta.setLodestoneTracked(false);

        for (Player player : playerList) {
            for (ItemStack item : player.getInventory()) {
                if (item != null && item.getType() == Material.COMPASS) {
                    item.setItemMeta(compassMeta);
                }
            }
        }
    }
}
