package org.wolflink.minecraft.plugin.siriuxa.spitem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.*;

/**
 * 特殊物品不考虑堆叠的情况！
 * 只通过描述辨认特殊物品
 */
public abstract class SpecialItem {
    /**
     * 不暴露给子类使用
     */
    private final SubScheduler subScheduler = new SubScheduler();
    private final Set<String> availableWorlds;
    private final Set<UUID> itemHolders = Collections.synchronizedSet(new HashSet<>());

    public SpecialItem(String... availableWorld) {
        availableWorlds = Collections.synchronizedSet(new HashSet<>(Arrays.stream(availableWorld).toList()));
    }

    public String getUniqueName() {
        return getClass().getSimpleName();
    }
    protected boolean enabled = false;

    public void enable() {
        if (enabled) return;
        enabled = true;
        inventoryScan();
        onEnable();
    }

    /**
     * 扫描玩家背包里是否含有该特殊物品
     * 如果有，则添加到 itemHolders
     */
    private void inventoryScan() {
        subScheduler.runTaskTimerAsync(()->{
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(!availableWorlds.contains(player.getWorld().getName())) continue;
                boolean hasItem = false;
                for (ItemStack itemStack : player.getInventory()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if(itemMeta == null) continue;
                    List<String> lores = itemMeta.getLore();
                    if(lores == null || lores.size() < 1) continue;
                    String uniqueLine = lores.get(0);
                    if(uniqueLine.endsWith(getUniqueName())) {
                        hasItem = true;
                        break;
                    }
                }
                UUID uuid = player.getUniqueId();
                if(hasItem) itemHolders.add(uuid);
                else itemHolders.remove(uuid);
            }
        },60,60);
    }
    public abstract void onEnable();
    public abstract void onDisable();
    public void disable() {
        if(!enabled) return;
        enabled = false;
        subScheduler.cancelAllTasks();
        onDisable();
    }
}
