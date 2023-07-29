package org.wolflink.minecraft.plugin.siriuxa.menu;

import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.view.StaticMenu;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class PublicMenuContainer {
    private final Map<Class<? extends StaticMenu>, StaticMenu> publicMenuMap = new HashMap<>();

    public PublicMenuContainer() {
        // 延迟初始化
//        publicMenuMap.put(DifficultyStaticMenu.class,null);
    }

    public boolean containMenu(Class<? extends StaticMenu> menuClass) {
        return publicMenuMap.containsKey(menuClass);
    }

    public Object getMenu(Class<? extends StaticMenu> menuClass) {
        return publicMenuMap.computeIfAbsent(menuClass, IOC::getBean);
    }

    @Nullable
    public Object getMenu(String title) {
        for (StaticMenu staticMenu : publicMenuMap.values()) {
            if (staticMenu == null) continue;
            if (staticMenu.getTitle().equals(title)) return staticMenu;
        }
        return null;
    }
}
