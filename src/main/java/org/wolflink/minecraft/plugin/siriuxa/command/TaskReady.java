package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskService;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class TaskReady extends WolfirdCommand {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private TaskService taskService;

    public TaskReady() {
        super(true, false, true, "sx task ready", "开始任务");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        Task task = taskRepository.findByGlobalTeamPlayer(player);
        taskService.ready(task).show(player);
    }
}
