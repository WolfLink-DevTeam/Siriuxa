package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskService;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class ForceFailedTask extends WolfirdCommand {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private TaskService taskService;
    public ForceFailedTask() {
        super(true, true, true, "sx task failed {player}", "将指定玩家的任务强制结束，结果为任务失败。");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        Player player = Bukkit.getPlayer(strings[0]);
        String result = "";
        if(player == null) {
            result = "未找到玩家 "+strings[0];
        } else {
            result = taskService.forceFailedTask(player).msg();
        }
        if(commandSender instanceof Player sender) Notifier.chat(result,sender);
        if(commandSender instanceof ConsoleCommandSender) Notifier.info(result);
    }
}
