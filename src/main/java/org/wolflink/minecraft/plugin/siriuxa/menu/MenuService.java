package org.wolflink.minecraft.plugin.siriuxa.menu;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.view.StaticMenu;

@Singleton
public class MenuService {

    @Inject
    private PublicMenuContainer publicMenuContainer;

    @NonNull
    public <T extends StaticMenu> T findMenu(Player player, Class<? extends StaticMenu> menuClass) {
        if (publicMenuContainer.containMenu(menuClass)) return (T) publicMenuContainer.getMenu(menuClass);
        StaticMenu result = PlayerMenuContainer.findMenu(player, menuClass);
        if (result == null) throw new IllegalStateException("不存在的菜单类：" + menuClass.getName());
        return (T) result;
    }

    public StaticMenu findMenu(Player player, String title) {
        StaticMenu staticMenu = (StaticMenu) publicMenuContainer.getMenu(title);
        if (staticMenu != null) return staticMenu;
        return PlayerMenuContainer.findMenu(player, title);
    }

    public void display(Class<? extends StaticMenu> menuClass, Player player) {
        StaticMenu staticMenu = findMenu(player, menuClass);
        display(staticMenu, player);
    }

    public void display(StaticMenu staticMenu, Player player) {
        staticMenu.display(player);
    }
}
