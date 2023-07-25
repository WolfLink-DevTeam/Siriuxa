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
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationCommandSender;
import org.wolflink.minecraft.plugin.siriuxa.api.world.WorldEditAPI;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

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

    // 用于设置指南针的SubScheduler
    private final SubScheduler subScheduler;

    // 用于存贮指南针已经生效的玩家的列表
    private final Set<Player> compassPlayers;

    public EvacuationZone(Task task, World world, int x, int z, int safeRadius) {
        this.task = task;
        this.center = new Location(world, x, world.getHighestBlockYAt(x, z) + 25D, z);
        this.safeRadius = safeRadius;
        this.locationCommandSender = new LocationCommandSender(center);
        this.subScheduler = new SubScheduler();
        this.compassPlayers = new HashSet<>();
    }

    public void setAvailable(boolean value) {
        if (available == value) return;
        available = value;
        if (available) {
            generateSchematic();
            subScheduler.runTaskTimer(() -> {
                for (Player player : task.getTaskPlayers()) {
                    if (!compassPlayers.contains(player)) {
                        setPlayerCompass(player, true);
                        compassPlayers.add(player);
                    }
                }
            }, 20L, 20L);
        } else {
            undoSchematic();
            subScheduler.cancelAllTasks();
            for (Player player : compassPlayers) {
                setPlayerCompass(player, false);
            }
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
        Notifier.broadcastChat(task.getTaskPlayers(), "飞艇已停留至坐标 X：" + center.getBlockX() + " Z：" + center.getBlockZ() + " 附近，如有需要请尽快前往撤离。");
        Notifier.broadcastChat(task.getTaskPlayers(), "温馨提示：提前在物品栏准备好指南针，为你的撤离之旅雪中送炭。=w=");
    }

    public void undoSchematic() {
        getPlayerInZone().forEach(task::evacuate);
        Notifier.broadcastChat(task.getTaskPlayers(), "坐标 X：" + center.getBlockX() + " Z：" + center.getBlockZ() + " 附近的飞艇已撤离，请等待下一艘飞艇接应。");
        IOC.getBean(WorldEditAPI.class).undoPaste(locationCommandSender, editSession);
    }

    public void setPlayerCompass(Player player, boolean available) {
        CompassMeta compassMeta = prepareCompassMeta(available);
        if (compassMeta == null) return;
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.getType() == Material.COMPASS) {
                item.setItemMeta(compassMeta);
            }
        }
    }

    @Nullable
    private CompassMeta prepareCompassMeta(boolean available) {
        CompassMeta compassMeta = (CompassMeta) new ItemStack(Material.COMPASS).getItemMeta();
        if (compassMeta == null) {
            Notifier.warn("获取撤离指南针的itemMeta失败");
            return null;
        }
        if (available) {
            compassMeta.setDisplayName("§a飞艇指南针");
            compassMeta.setLore(List.of("§f ", "  §7接收到了神奇的信号，指向最近的撤离飞艇", "§f "));
            compassMeta.setLodestone(center);
        } else compassMeta.setLodestone(Objects.requireNonNull(task.getTaskRegion()).getCenter());
        compassMeta.setLodestoneTracked(false);
        return compassMeta;
    }
}

