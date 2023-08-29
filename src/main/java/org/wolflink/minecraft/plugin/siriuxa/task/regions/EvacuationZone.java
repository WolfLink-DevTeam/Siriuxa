package org.wolflink.minecraft.plugin.siriuxa.task.regions;

import com.sk89q.worldedit.EditSession;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationCommandSender;
import org.wolflink.minecraft.plugin.siriuxa.api.world.WorldEditAPI;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration.ExplorationTask;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

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
     * 撤离的安全区域半径
     */
    private final int safeRadius;
    /**
     * 归属的任务(只能是自由勘探任务类型)
     */
    private final ExplorationTask explorationTask;
    // 用于设置指南针的SubScheduler
    private final SubScheduler subScheduler;
    /**
     * 是否可用
     */
    private boolean available = false;
    private EditSession editSession;

    public EvacuationZone(ExplorationTask explorationTask, World world, int x, int z, int safeRadius) {
        this.explorationTask = explorationTask;
        this.center = new Location(world, x, world.getHighestBlockYAt(x, z) + 25D, z);
        this.safeRadius = safeRadius;
        this.locationCommandSender = new LocationCommandSender(center);
        this.subScheduler = new SubScheduler();
    }

    private boolean hasDefaultCompass(Player player) {
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack == null) continue;
            if (itemStack.getType() != Material.COMPASS) continue;
            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null || meta.getLore() == null) return true;
        }
        return false;
    }

    public void setAvailable(boolean value) {
        if (available == value) return;
        available = value;
        if (available) {
            generateSchematic();
            subScheduler.runTaskTimer(() -> {
                for (Player player : explorationTask.getTaskPlayers()) {
                    // 有指南针，并且指南针没有被激活
                    if (hasDefaultCompass(player)) {
                        setPlayerCompass(player, true);
                    }
                }
            }, 20L, 20L);
        } else {
            undoSchematic();
            subScheduler.cancelAllTasks();
            for (Player player : explorationTask.getTaskPlayers()) {
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

        for (Player player : explorationTask.getTaskPlayers()) {
            Location pLoc = player.getLocation();
            if (pLoc.getWorld() != center.getWorld()) continue;
            if (pLoc.distance(center) <= safeRadius && pLoc.clone().getBlock().getType().equals(Material.END_PORTAL_FRAME)) {
                playerSet.add(player);
            }
        }
        return playerSet;
    }

    public void generateSchematic() {
        editSession = IOC.getBean(WorldEditAPI.class).pasteEvacuationUnit(locationCommandSender);
        Notifier.broadcastChat(explorationTask.getTaskPlayers(), "飞艇已停留至坐标 X：" + center.getBlockX() + " Z：" + center.getBlockZ() + " 附近，如有需要请尽快前往撤离。");
        Notifier.broadcastChat(explorationTask.getTaskPlayers(), "温馨提示：提前在物品栏准备好指南针，为你的撤离之旅雪中送炭。=w=");
    }

    public void undoSchematic() {
        getPlayerInZone().forEach(explorationTask::evacuate);
        Notifier.broadcastChat(explorationTask.getTaskPlayers(), "坐标 X：" + center.getBlockX() + " Z：" + center.getBlockZ() + " 附近的飞艇已撤离，请等待下一艘飞艇接应。");
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
        if (!available) return compassMeta;
        if (compassMeta == null) {
            Notifier.warn("获取撤离指南针的itemMeta失败");
            return null;
        }
        compassMeta.setDisplayName("§a飞艇指南针");
        compassMeta.setLore(List.of("§f ", "  §7接收到了神奇的信号，指向最近的撤离飞艇", "§f "));
        compassMeta.setLodestone(center);
        compassMeta.setLodestoneTracked(false);
        return compassMeta;
    }
}

