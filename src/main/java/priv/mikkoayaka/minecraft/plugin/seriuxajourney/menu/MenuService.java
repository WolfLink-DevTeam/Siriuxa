package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.Menu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.TaskMenu;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class MenuService {

    // 公共菜单Map
    private static final Map<Class<? extends Menu>,Menu> publicMenuMap = new HashMap<>();

    @NonNull
    public <T> T findMenu(Player player,Class<? extends Menu> menuClass) {
        if(publicMenuMap.containsKey(menuClass)) return (T) publicMenuMap.get(menuClass);
        Menu result = PlayerMenuContainer.findMenu(player,menuClass);
        if(result == null)throw new IllegalStateException("不存在的菜单类："+menuClass.getName());
        return (T) result;
    }

    public void display(Class<? extends Menu> menuClass,Player player) {
        Menu menu = findMenu(player,menuClass);
        display(menu,player);
    }
    public void display(Menu menu, Player player) {
        menu.display(player);
    }
    public void selectDifficulty(Player player, TaskDifficulty taskDifficulty) {
        TaskMenu taskMenu = findMenu(player, TaskMenu.class);
        taskMenu.setTaskDifficulty(taskDifficulty);
    }
}
