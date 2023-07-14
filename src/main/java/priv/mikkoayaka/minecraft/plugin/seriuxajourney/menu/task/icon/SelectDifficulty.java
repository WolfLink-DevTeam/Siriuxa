package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.ItemIcon;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.MenuService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.difficulty.DifficultyMenu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.TaskMenu;

public class SelectDifficulty extends ItemIcon {
    private final TaskMenu taskMenu;
    private final MenuService menuService;
    public SelectDifficulty(TaskMenu taskMenu) {
        super(false);
        this.menuService = IOC.getBean(MenuService.class);
        this.taskMenu = taskMenu;
    }

    @Override
    public void leftClick(Player player) {
        menuService.display(DifficultyMenu.class,player);
    }

    @Override
    public void rightClick(Player player) {

    }

    @Override
    protected @NonNull ItemStack createIcon() {
        String difficultyName = "§7未指定";
        Material material = Material.ITEM_FRAME;
        if(taskMenu.getExplorationDifficulty() != null) {
            difficultyName = taskMenu.getExplorationDifficulty().getColor()+taskMenu.getExplorationDifficulty().getName();
            material = taskMenu.getExplorationDifficulty().getIcon();
        }
        return fastCreateItemStack(material,1,"§8[ §f当前难度 §8] §r"+difficultyName,
                " ",
                "  §7想要轻松完成任务，还是尝试挑战困难？",
                " "
                );
    }
}
