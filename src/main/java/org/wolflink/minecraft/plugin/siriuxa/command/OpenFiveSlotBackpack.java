package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.menu.MenuService;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.FiveSlotBackpackMenu;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class OpenFiveSlotBackpack extends WolfirdCommand {
    @Inject
    private TaskRepository taskRepository;

    public OpenFiveSlotBackpack() {
        super(true, false, true, "sx menu five_slot_backpack", "打开5格背包界面");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        IOC.getBean(MenuService.class).display(FiveSlotBackpackMenu.class,player);
    }
}
