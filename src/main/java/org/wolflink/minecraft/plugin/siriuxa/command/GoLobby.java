package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskService;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class GoLobby extends WolfirdCommand {
    public GoLobby() {
        super(false, false, true, "sx lobby", "回到任务大厅");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        // 在任务世界
        if (player.getWorld().getName().equalsIgnoreCase("normal-exploration")) {
            Task task = IOC.getBean(TaskRepository.class).findByTaskTeamPlayer(player);
            if (task != null) {
                Notifier.chat("你当前还处在任务中，无法离开任务世界。", player);
                return;
            }
            IOC.getBean(TaskService.class).goLobby(player);
        }
    }
}
