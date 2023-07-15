package priv.mikkoayaka.minecraft.plugin.seriuxajourney.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.MenuService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.TaskRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationTask;

@Singleton
public class TaskReady extends WolfirdCommand {
    @Inject
    private MenuService menuService;
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private ExplorationService explorationService;
    public TaskReady() {
        super(true, false, true, "sj task ready", "开始任务");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        ExplorationTask explorationTask = taskRepository.findByPlayer(ExplorationTask.class,player);
        explorationService.readyTask(explorationTask).show(player);
    }
}
