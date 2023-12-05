package org.wolflink.minecraft.plugin.siriuxa.api.view;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 动态布局菜单
 * 菜单的布局在菜单创建后仍然可以改变
 */
@Getter
public abstract class DynamicMenu extends Menu {
    private final long refreshTick;

    protected DynamicMenu(UUID ownerUuid, String title, int size, long refreshTick) {
        this(ownerUuid, title, size, refreshTick, new HashSet<>());
    }

    protected DynamicMenu(UUID ownerUuid, String title, int size, long refreshTick, Set<Integer> containerSlots) {
        super(ownerUuid, title, size, containerSlots);
        this.refreshTick = refreshTick;
        if (refreshTick > 0)
            Bukkit.getScheduler().runTaskTimer(Siriuxa.getInstance(), this::refreshLayout, refreshTick, refreshTick);
    }

    /**
     * 刷新菜单布局
     * 动态 Icon 注册计时器
     */
    @Override
    protected void refreshLayout() {
        if (getOwner() == null || !getOwner().isOnline()) return;
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {
            initIcons();
            overrideIcons();
            bindItems();
        });
    }
}
