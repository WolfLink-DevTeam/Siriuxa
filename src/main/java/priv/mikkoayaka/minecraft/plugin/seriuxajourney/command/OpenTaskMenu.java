package priv.mikkoayaka.minecraft.plugin.seriuxajourney.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.MenuService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.TaskMenu;

@Singleton
public class OpenTaskMenu extends WolfirdCommand {
    @Inject
    private MenuService menuService;

    public OpenTaskMenu() {
        super(true, false, true, "sj menu task", "打开任务菜单");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        menuService.display(TaskMenu.class, player);
    }
}
