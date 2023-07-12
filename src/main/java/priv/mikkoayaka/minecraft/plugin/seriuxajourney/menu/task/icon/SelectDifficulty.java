package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.ItemIcon;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.TaskMenu;

public class SelectDifficulty extends ItemIcon {
    private final TaskMenu taskMenu;
    public SelectDifficulty(TaskMenu taskMenu) {
        super(false);
        this.taskMenu = taskMenu;
    }

    @Override
    public void leftClick(Player player) {

    }

    @Override
    public void rightClick(Player player) {

    }

    @Override
    protected @NonNull ItemStack createIcon() {
        String difficultyName = "未指定";
        if(taskMenu.getTaskDifficulty() != null) {
            difficultyName = taskMenu.getTaskDifficulty().name();
        }
        return fastCreateItemStack(Material.ITEM_FRAME,1,"§8[ §f当前难度 §8] §r"+difficultyName,
                " ",
                "  §7想要轻松完成任务，还是尝试挑战困难？",
                " ",
                "  §a点击选择",
                " "
                );
    }
}
