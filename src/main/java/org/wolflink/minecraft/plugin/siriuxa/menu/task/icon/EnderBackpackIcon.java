package org.wolflink.minecraft.plugin.siriuxa.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Icon;
import org.wolflink.minecraft.plugin.siriuxa.menu.MenuService;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.enderbackpack.EnderBackpackMenu;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.BaseWaitStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;

public class EnderBackpackIcon extends Icon {

    public EnderBackpackIcon() {
        super(10);
    }

    @Override
    protected @NonNull ItemStack createIcon() {
        return fastCreateItemStack(Material.BUNDLE, 1, "§8[ §a行李托运 §8]",
                " ",
                "  §7任务区域与主城之间有着非常遥远的距离，",
                "  §7冒险家们需要乘坐空降仓空降到地面进行任务。",
                "  §7而空降仓的反推燃料有限，",
                "  §7所以冒险家们只能通过行李托运的方式运送物资，",
                "  §7否则会因为加速度过大导致降落时严重受伤。",
                "  ",
                "  §8(§f任务开始后，其中的物资将被发放到你的背包§8)",
                " ");
    }

    @Override
    public void leftClick(Player player) {
        Task task = IOC.getBean(TaskRepository.class).findByGlobalTeamPlayer(player);
        if (task != null && !(task.getStageHolder().getThisStage() instanceof BaseWaitStage)) {
            Notifier.chat("你的队伍还在任务中，请等待任务结束后查看安全背包。", player);
            return;
        }
        IOC.getBean(MenuService.class).display(EnderBackpackMenu.class, player);
    }

    @Override
    public void rightClick(Player player) {

    }
}
