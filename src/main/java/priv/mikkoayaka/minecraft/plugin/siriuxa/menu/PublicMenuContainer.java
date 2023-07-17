package priv.mikkoayaka.minecraft.plugin.siriuxa.menu;

import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.siriuxa.api.view.Menu;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class PublicMenuContainer {
    private final Map<Class<? extends Menu>, Menu> publicMenuMap = new HashMap<>();

    public PublicMenuContainer() {
        // 延迟初始化
//        publicMenuMap.put(DifficultyMenu.class,null);
    }

    public boolean containMenu(Class<? extends Menu> menuClass) {
        return publicMenuMap.containsKey(menuClass);
    }

    public Object getMenu(Class<? extends Menu> menuClass) {
        if (publicMenuMap.get(menuClass) == null) {
            publicMenuMap.put(menuClass, IOC.getBean(menuClass));
        }
        return publicMenuMap.get(menuClass);
    }

    @Nullable
    public Object getMenu(String title) {
        for (Menu menu : publicMenuMap.values()) {
            if (menu == null) continue;
            if (menu.getTitle().equals(title)) return menu;
        }
        return null;
    }
}
