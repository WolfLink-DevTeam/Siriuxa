package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.menu.MenuService;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.enderbackpack.EnderBackpackMenu;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration.taskstage.WaitStage;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class OpenEnderBackpack extends WolfirdCommand {
    @Inject
    private TaskRepository taskRepository;

    public OpenEnderBackpack() {
        super(true, false, true, "sx menu five_slot_backpack", "打开行李托运界面");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        Task task = taskRepository.findByGlobalTeamPlayer(player);
        if (task != null && !(task.getStageHolder().getThisStage() instanceof WaitStage)) {
            Notifier.chat("你的队伍还在任务中，请等待任务结束后查看安全背包。", player);
            return;
        }
        IOC.getBean(MenuService.class).display(EnderBackpackMenu.class, player);
    }
}
