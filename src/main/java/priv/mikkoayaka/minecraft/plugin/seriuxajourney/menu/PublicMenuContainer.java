package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu;

import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.Menu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.difficulty.DifficultyMenu;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class PublicMenuContainer {
    private final Map<Class<? extends Menu>,Menu> publicMenuMap = new HashMap<>();

    public PublicMenuContainer() {
        // 延迟初始化
        publicMenuMap.put(DifficultyMenu.class,null);
    }
    public boolean containMenu(Class<? extends Menu> menuClass) {
        return publicMenuMap.containsKey(menuClass);
    }
    public Object getMenu(Class<? extends Menu> menuClass) {
        if(publicMenuMap.get(menuClass) == null) {
            publicMenuMap.put(menuClass, IOC.getBean(menuClass));
        }
        return publicMenuMap.get(menuClass);
    }
}
