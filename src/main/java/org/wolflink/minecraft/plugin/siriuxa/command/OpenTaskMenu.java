package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.menu.MenuService;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.TaskStaticMenu;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class OpenTaskMenu extends WolfirdCommand {
    @Inject
    private MenuService menuService;

    public OpenTaskMenu() {
        super(true, false, true, "sx menu task", "打开任务菜单");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        menuService.display(TaskStaticMenu.class, player);
    }
}
