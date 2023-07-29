package org.wolflink.minecraft.plugin.siriuxa.api.view;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * 菜单父类
 * 可能是静态菜单，也可能是动态菜单
 */
public abstract class Menu {
    protected final SubScheduler subScheduler = new SubScheduler();
    protected final Inventory inventory;
    @Getter
    protected final String title;
    @Getter
    protected final int size;
    @Getter
    protected final UUID ownerUuid;
    protected Icon[] icons;
    protected Menu(UUID ownerUuid, String title, int size) {
        this.ownerUuid = ownerUuid;
        this.title = title;
        this.size = size;
        inventory = Bukkit.createInventory(null, size, title);
        icons = null;
    }
    @Nullable
    public Player getOwner() {
        Player player = Bukkit.getPlayer(ownerUuid);
        if (player == null || !player.isOnline()) return null;
        return player;
    }
    @NonNull
    public OfflinePlayer getOfflineOwner() {
        return Bukkit.getOfflinePlayer(ownerUuid);
    }
    protected abstract void refreshLayout();
    /**
     * 将菜单展示给玩家
     */
    public void display(Player player) {
        refreshLayout();
        player.closeInventory();
        player.openInventory(inventory);
    }
    /**
     * 子类实现 ItemIcon
     */
    protected abstract void overrideIcons();

    public void setIcon(int index, Icon icon) {
        if (index >= icons.length || index < 0) return;
        icons[index] = icon;
    }

    public Icon getIcon(int index) {
        if (index >= icons.length || index < 0) return null;
        return icons[index];
    }
    /**
     * 格式化背包菜单，填充边界，空气等
     */
    protected void initIcons() {
        icons = new Icon[size];
        EmptyIcon emptyItemIcon = IOC.getBean(EmptyIcon.class);
        for (int i = 0; i < size; i++) {
            setIcon(i, emptyItemIcon);
        }
        BorderIcon borderIcon = IOC.getBean(BorderIcon.class);
        if (size == 27) {
            Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 19, 20, 21, 22, 23, 24, 25, 26)
                    .forEach(index -> setIcon(index, borderIcon));
        }
        if (size == 54) {
            Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 53, 52, 51, 50, 49, 48, 47, 46, 45)
                    .forEach(index -> setIcon(index, borderIcon));
        }
    }

    /**
     * 将图标的物品绑定到菜单中
     */
    protected void bindItems() {
        subScheduler.cancelAllTasks();
        for (int i = 0; i < size; i++) {
            Icon icon = getIcon(i);
            inventory.setItem(i, icon.getIcon());
            long refreshTick = icon.getRefreshTick();
            if(refreshTick > 0) {
                final int finalI = i;
                subScheduler.runTaskTimerAsync(()->inventory.setItem(finalI,icon.getIcon()),refreshTick,refreshTick);
            }
        }
    }
}
