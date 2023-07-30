package org.wolflink.minecraft.plugin.siriuxa.api.view;

import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * 静态布局菜单
 * 菜单的布局一旦创建则无法改变
 * 图标仍然可以刷新
 */
public abstract class StaticMenu extends Menu {
    /**
     * 菜单只会在打开时刷新一次
     */

    protected StaticMenu(UUID ownerUuid, String title, int size) {
        super(ownerUuid,title,size);
    }
    protected StaticMenu(UUID ownerUuid, String title, int size, Set<Integer> containerSlots) {
        super(ownerUuid,title,size,containerSlots);
    }

    /**
     * 刷新菜单布局
     * 动态 Icon 注册计时器
     */
    @Override
    protected void refreshLayout() {
        if(getOwner() == null || !getOwner().isOnline()) return;
        if(icons == null) {
            initIcons();
            overrideIcons();
            bindItems();
        }
    }
}
