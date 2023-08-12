package org.wolflink.minecraft.plugin.siriuxa.api.view;


import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.*;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.menu.MenuService;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class MenuEventListener extends WolfirdListener {
    @Inject
    MenuService menuService;

    private final Set<Player> cooldownPlayers = Collections.synchronizedSet(new HashSet<>());
    @EventHandler
    void onService(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        String title = e.getView().getTitle();
        Menu menu = menuService.findMenu(p, title);
        if (menu == null) return;
        Icon icon = menu.getIcon(e.getSlot());
        if (icon == null) return;
        invokeViewClick(p, icon, e.getClick());
    }
    @EventHandler
    void onClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        HumanEntity humanEntity = event.getPlayer();
        if(humanEntity instanceof Player player) {
            Menu menu = menuService.findMenu(player,title);
            if(menu == null) return;
            menu.onClose(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onProtect(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player p)) {
            return;
        }
        Menu menu = menuService.findMenu(p, e.getView().getTitle());
        if(menu == null) return;
        // 防止把物品移动到菜单中
        if(menu.containerSlots.size() == 0) {
            e.setCancelled(true);
            return;
        }
        // 点击自己的背包
        if (p.getInventory().equals(e.getClickedInventory())) {
            return;
        }
        if(cooldownPlayers.contains(p)) {
            e.setCancelled(true);
            return;
        }
        cooldownPlayers.add(p);
        getSubScheduler().runTaskLaterAsync(()->cooldownPlayers.remove(p),3);
        if (menu.containerSlots.contains(e.getSlot())) {
            return;
        }
        e.setCancelled(true);
    }

    private void invokeViewClick(Player player, Icon icon, ClickType clickType) {
        if (clickType.isLeftClick()) icon.leftClick(player);
        if (clickType.isRightClick()) icon.rightClick(player);
    }
}
