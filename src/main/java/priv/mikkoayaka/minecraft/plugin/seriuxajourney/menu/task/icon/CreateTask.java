package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.ItemIcon;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.TaskMenu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.ExplorationService;

public class CreateTask extends ItemIcon {

    private final ExplorationService explorationService;
    private final TaskMenu taskMenu;
    public CreateTask(TaskMenu taskMenu) {
        super(false);
        this.taskMenu = taskMenu;
        explorationService = IOC.getBean(ExplorationService.class);
    }

    @Override
    protected @NonNull ItemStack createIcon() {
        if(canCreate()) {
            return fastCreateItemStack(Material.WRITABLE_BOOK,1,"§8[ §f登记任务 §8]",
                    " ",
                    "  §7完成任务登记后，前往 §f空降勘探仓 §7，准备降落。",
                    "  §7当然，你可以在一旁的补给处再做一些准备，购买物资什么的。",
                    "  §7祝你好运！",
                    " ",
                    "  §a任务可登记",
                    " "
            );
        } else {
            return fastCreateItemStack(Material.WRITABLE_BOOK,1,"§8[ §f登记任务 §8]",
                    " ",
                    "  §7完成任务登记后，前往 §f空降勘探仓 §7，准备降落。",
                    "  §7当然，你可以在一旁的补给处再做一些准备，购买物资什么的。",
                    "  §7祝你好运！",
                    " ",
                    "  §c任务不可登记",
                    "  §8|| §e未选择任务难度"
            );
        }
    }

    private boolean canCreate() {
        return taskMenu.getTaskDifficulty() != null;
    }
    @Override
    public void leftClick(Player player) {
        if(!canCreate())return;
        explorationService.createTask(player,taskMenu.getTaskDifficulty()).show(player);
    }

    @Override
    public void rightClick(Player player) {

    }
}
