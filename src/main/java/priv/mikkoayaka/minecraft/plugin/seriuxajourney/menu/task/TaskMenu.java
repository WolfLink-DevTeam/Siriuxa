package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task;

import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.BorderIcon;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.Menu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.ExplorationDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.MenuService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.difficulty.ExplorationDifficultyMenu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.icon.CreateTask;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.icon.SelectDifficulty;

import java.util.UUID;

public class TaskMenu extends Menu {

    private final MenuService menuService;
    public TaskMenu(UUID uuid) {
        super(uuid,-1, "§0§l任务菜单", 27);
        menuService = IOC.getBean(MenuService.class);
    }

    @Override
    public void overrideIcons() {
        BorderIcon borderIcon = IOC.getBean(BorderIcon.class);
        setIcon(9, borderIcon);
        setIcon(15, borderIcon);
        setIcon(17, borderIcon);
        setIcon(10, new SelectDifficulty(this));
        setIcon(16, new CreateTask(this));
    }

    public ExplorationDifficulty getSelectedDifficulty() {
        ExplorationDifficultyMenu explorationDifficultyMenu = menuService.findMenu(getOwner(), ExplorationDifficultyMenu.class);
        return explorationDifficultyMenu.getSelectedDifficulty();
    }
}
