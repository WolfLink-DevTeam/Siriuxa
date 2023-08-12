package org.wolflink.minecraft.plugin.siriuxa.api.view;


import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
        cooldownPlayers.add(p);
        getSubScheduler().runTaskLaterAsync(()->cooldownPlayers.remove(p),10);
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
        // 点击自己的背包
        if (e.getWhoClicked().getInventory().equals(e.getClickedInventory())) {
            return;
        }
        String title = e.getWhoClicked().getOpenInventory().getTitle();
        Menu menu = menuService.findMenu((Player) e.getWhoClicked(), title);
        if (menu != null) {
            if(menu.containerSlots.contains(e.getSlot())) return;
            e.setCancelled(true);
        }
    }

    private void invokeViewClick(Player player, Icon icon, ClickType clickType) {
        if (clickType.isLeftClick()) icon.leftClick(player);
        if (clickType.isRightClick()) icon.rightClick(player);
    }
}
