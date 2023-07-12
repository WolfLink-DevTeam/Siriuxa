package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.MenuService;

@Singleton
public class MenuEventListener extends WolfirdListener {
    @Inject
    MenuService menuService;
    @EventHandler
    void onService(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        Menu menu = menuService.findMenu(p,title);
        if(menu == null)return;
        ItemIcon itemIcon = menu.getIcon(e.getSlot());
        invokeViewClick(p,itemIcon,e.getClick());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    void onProtect(InventoryClickEvent e) {
        // 点击自己的背包
        if(e.getWhoClicked().getInventory().equals(e.getClickedInventory())) {
            return;
        }
        String title = e.getWhoClicked().getOpenInventory().getTitle();
        Menu menu = menuService.findMenu((Player) e.getWhoClicked(),title);
        if(menu != null) e.setCancelled(true);
    }
    private void invokeViewClick(Player player, ItemIcon itemIcon, ClickType clickType) {
        if(clickType.isLeftClick())itemIcon.leftClick(player);
        if(clickType.isRightClick())itemIcon.rightClick(player);
    }
}
