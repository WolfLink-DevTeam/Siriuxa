package org.wolflink.minecraft.plugin.siriuxa.api.view;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;

import java.util.UUID;

/**
 * 动态布局菜单
 * 菜单的布局在菜单创建后仍然可以改变
 */
public abstract class DynamicMenu extends Menu {
    @Getter
    private final long refreshTick;
    protected DynamicMenu(UUID ownerUuid, String title, int size,long refreshTick) {
        super(ownerUuid,title,size);
        this.refreshTick = refreshTick;
        Bukkit.getScheduler().runTaskTimer(Siriuxa.getInstance(),this::refreshLayout,refreshTick,refreshTick);
    }
    /**
     * 刷新菜单布局
     * 动态 Icon 注册计时器
     */
    @Override
    protected void refreshLayout() {
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(),()->{
            initIcons();
            overrideIcons();
            bindItems();
        });

    }
}
