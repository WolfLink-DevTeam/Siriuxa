package org.wolflink.minecraft.plugin.siriuxa.menu;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Menu;

@Singleton
public class MenuService {

    @Inject
    private PublicMenuContainer publicMenuContainer;

    @NonNull
    public <T extends Menu> T findMenu(Player player, Class<? extends Menu> menuClass) {
        if (publicMenuContainer.containMenu(menuClass)) return (T) publicMenuContainer.getMenu(menuClass);
        Menu result = PlayerMenuContainer.findMenu(player, menuClass);
        if (result == null) throw new IllegalStateException("不存在的菜单类：" + menuClass.getName());
        return (T) result;
    }

    public Menu findMenu(Player player, String title) {
        Menu staticMenu = (Menu) publicMenuContainer.getMenu(title);
        if (staticMenu != null) return staticMenu;
        return PlayerMenuContainer.findMenu(player, title);
    }

    public void display(Class<? extends Menu> menuClass, Player player) {
        Menu menu = findMenu(player, menuClass);
        display(menu, player);
    }

    public void display(Menu menu, Player player) {
        menu.display(player);
    }
}
