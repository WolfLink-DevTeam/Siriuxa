package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task;

import lombok.Getter;
import lombok.Setter;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.InventoryMenu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.icon.SelectDifficulty;

import java.util.UUID;

public class TaskMenu extends InventoryMenu {

    private final UUID uuid;

    @Getter
    @Setter
    private TaskDifficulty taskDifficulty = null;
    public TaskMenu(UUID uuid) {
        super(-1, "§0任务菜单", 27);
        this.uuid = uuid;
    }

    @Override
    public void updateContent() {
        getContent()[10] = new SelectDifficulty(this);
    }
}
