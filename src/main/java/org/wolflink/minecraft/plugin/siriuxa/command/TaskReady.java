package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRepository;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskService;

@Singleton
public class TaskReady extends WolfirdCommand {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private TaskService taskService;

    public TaskReady() {
        super(true, false, true, "sj task ready", "开始任务");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        Task task = taskRepository.findByPlayer(player);
        taskService.ready(task).show(player);
    }
}
