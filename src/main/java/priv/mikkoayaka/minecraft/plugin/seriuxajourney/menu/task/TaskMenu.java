package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task;

import lombok.Getter;
import lombok.Setter;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.Border;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.Menu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.ExplorationDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.icon.CreateTask;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.icon.SelectDifficulty;

import java.util.UUID;

public class TaskMenu extends Menu {

    private final UUID uuid;

    @Getter
    @Setter
    private ExplorationDifficulty explorationDifficulty = null;

    public TaskMenu(UUID uuid) {
        super(-1, "§0§l任务菜单", 27);
        this.uuid = uuid;
    }

    @Override
    public void overrideIcons() {
        Border border = IOC.getBean(Border.class);
        setIcon(9, border);
        setIcon(15, border);
        setIcon(17, border);
        setIcon(10, new SelectDifficulty(this));
        setIcon(16, new CreateTask(this));
    }
}
