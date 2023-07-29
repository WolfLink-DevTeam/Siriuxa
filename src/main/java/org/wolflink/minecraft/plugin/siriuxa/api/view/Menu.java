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

public abstract class Menu {
    private final Inventory inventory;
    @Getter
    private final String title;
    @Getter
    private final int size;
    @Getter
    private final UUID ownerUuid;
    private Icon[] icons;

    /**
     * 菜单只会在打开时刷新一次
     */
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

    /**
     * 格式化背包菜单，填充边界，空气等
     */
    private void initIcons() {
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

    private final SubScheduler subScheduler = new SubScheduler();
    /**
     * 初始化整个菜单
     * 动态 Icon 注册计时器
     */
    protected void init() {
        if(icons == null) {
            initIcons();
            overrideIcons();
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
     * 将菜单展示给玩家
     */
    public void display(Player player) {
        init();
        player.closeInventory();
        player.openInventory(inventory);
    }
}
