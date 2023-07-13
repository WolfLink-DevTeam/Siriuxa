package priv.mikkoayaka.minecraft.plugin.seriuxajourney.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.MenuService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationTask;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationTaskRepository;

@Singleton
public class Debug extends WolfirdCommand {
    @Inject
    private MenuService menuService;
    @Inject
    private ExplorationTaskRepository explorationTaskRepository;
    @Inject
    private ExplorationService explorationService;
    public Debug() {
        super(true, false, true, "sj debug", "开发者调试指令");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        ExplorationTask explorationTask = explorationTaskRepository.findByPlayer(player);
        explorationService.startTask(explorationTask).show(player);
    }
}
