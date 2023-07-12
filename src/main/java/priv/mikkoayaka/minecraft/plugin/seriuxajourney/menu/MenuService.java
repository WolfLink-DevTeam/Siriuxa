package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.Menu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.TaskMenu;

@Singleton
public class MenuService {

    @Inject
    private PublicMenuContainer publicMenuContainer;

    @NonNull
    public <T extends Menu> T findMenu(Player player,Class<? extends Menu> menuClass) {
        if(publicMenuContainer.containMenu(menuClass)) return (T) publicMenuContainer.getMenu(menuClass);
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
