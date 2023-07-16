package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.Icon;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.ExplorationDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.MenuService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.difficulty.ExplorationDifficultyMenu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.TaskMenu;

public class SelectDifficulty extends Icon {
    private final TaskMenu taskMenu;
    private final MenuService menuService;
    public SelectDifficulty(TaskMenu taskMenu) {
        super(false);
        this.menuService = IOC.getBean(MenuService.class);
        this.taskMenu = taskMenu;
    }

    @Override
    public void leftClick(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES,1f,1f);
        menuService.display(ExplorationDifficultyMenu.class,player);
    }

    @Override
    public void rightClick(Player player) {

    }

    @Override
    protected @NonNull ItemStack createIcon() {
        String difficultyName = "§7未指定";
        Material material = Material.ITEM_FRAME;
        Player player = taskMenu.getOwner();
        if(player == null || !player.isOnline()) return fastCreateItemStack(material,1,"§8[ §f当前难度 §8] §r玩家未在线");
        ExplorationDifficulty difficulty = taskMenu.getSelectedDifficulty();
        if(difficulty != null) {
            difficultyName = difficulty.getColor()+difficulty.getName();
            material = difficulty.getIcon();
        }
        return fastCreateItemStack(material,1,"§8[ §f当前难度 §8] §r"+difficultyName,
                " ",
                "  §7想要轻松完成任务，还是尝试挑战困难？",
                " "
                );
    }
}
