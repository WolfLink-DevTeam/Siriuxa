package org.wolflink.minecraft.plugin.siriuxa.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Icon;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.TaskStaticMenu;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.ExplorationTask;

public class CreateTask extends Icon {

    private final TaskService taskService;
    private final TaskStaticMenu taskMenu;

    public CreateTask(TaskStaticMenu taskMenu) {
        super(20);
        this.taskMenu = taskMenu;
        taskService = IOC.getBean(TaskService.class);
    }

    @Override
    protected @NonNull ItemStack createIcon() {
        if (canCreate()) {
            return fastCreateItemStack(Material.WRITABLE_BOOK, 1, "§8[ §f登记任务 §8]",
                    " ",
                    "  §7完成任务登记后，前往 §f空降勘探仓 §7，准备降落。",
                    "  §7当然，你可以在一旁的补给处再做一些准备，购买物资什么的。",
                    "  §7祝你好运！",
                    " ",
                    "  §a任务可登记",
                    " "
            );
        } else {
            return fastCreateItemStack(Material.WRITABLE_BOOK, 1, "§8[ §f登记任务 §8]",
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
        return taskMenu.getSelectedDifficulty() != null;
    }

    @Override
    public void leftClick(Player player) {
        if (!canCreate()) return;
        Result result = taskService.create(player, ExplorationTask.class, taskMenu.getSelectedDifficulty());
        result.show(player);
        player.closeInventory();
        if (result.result()) {
            player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.8f);
        }
    }

    @Override
    public void rightClick(Player player) {
        // 和左键单击相同的效果
        leftClick(player);
    }
}
