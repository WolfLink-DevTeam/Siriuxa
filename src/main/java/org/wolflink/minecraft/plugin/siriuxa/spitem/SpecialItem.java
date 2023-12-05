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
 * 例如护身符之类的被动道具
 */
public abstract class SpecialItem {
    /**
     * 不暴露给子类使用
     */
    private final SubScheduler subScheduler = new SubScheduler();
    private final Set<String> availableWorlds;
    protected final Set<UUID> itemHolders = Collections.synchronizedSet(new HashSet<>());

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
        refreshItemAbility();
        onEnable();
    }

    protected abstract boolean isThisSpecialItem(ItemStack itemStack);

    protected boolean compareUniqueLore(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        List<String> lores = itemMeta.getLore();
        if (lores == null || lores.size() < 1) return false;
        String uniqueLine = lores.get(0);
        return uniqueLine.endsWith(getUniqueName());
    }

    private void refreshItemAbility() {
        subScheduler.runTaskTimerAsync(() -> {
            for (UUID holderUuid : itemHolders) {
                Player player = Bukkit.getPlayer(holderUuid);
                if (player == null || !player.isOnline()) continue;
                applyItemAbility(player);
            }
        }, 60, 60);
    }

    /**
     * 扫描玩家背包里是否含有该特殊物品
     * 如果有，则添加到 itemHolders
     */
    private void inventoryScan() {
        subScheduler.runTaskTimerAsync(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!availableWorlds.contains(player.getWorld().getName())) continue;
                boolean hasItem = false;
                for (ItemStack itemStack : player.getInventory()) {
                    if (isThisSpecialItem(itemStack)) {
                        hasItem = true;
                        break;
                    }
                }
                UUID uuid = player.getUniqueId();
                if (hasItem) itemHolders.add(uuid);
                else itemHolders.remove(uuid);
            }
        }, 60, 60);
    }

    public abstract void applyItemAbility(Player holder);

    public abstract void onEnable();

    public abstract void onDisable();

    public void disable() {
        if (!enabled) return;
        enabled = false;
        subScheduler.cancelAllTasks();
        onDisable();
    }
}
