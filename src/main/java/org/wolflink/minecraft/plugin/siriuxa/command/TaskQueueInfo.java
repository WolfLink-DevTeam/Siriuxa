package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskQueue;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

import java.util.Calendar;

@Singleton
public class TaskQueueInfo extends WolfirdCommand {
    @Inject
    private TaskQueue taskQueue;

    public TaskQueueInfo() {
        super(false, false, true, "sx task queue", "查看当前任务队列状态");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        int secs = (int) ((Calendar.getInstance().getTimeInMillis() - taskQueue.getLastStarted().getTimeInMillis()) / 1000);
        Notifier.chat("§f服务器队列信息", player);
        player.sendMessage(" ");
        player.sendMessage("§f最近一组任务开始于 §a" + secs + " §f秒之前");
        player.sendMessage(" ");
        player.sendMessage("§f当前进行 §a" + taskQueue.getNowSize() + "组");
        player.sendMessage("§f最大容量 §a" + taskQueue.getMaxSize() + "组");
        player.sendMessage(" ");
    }
}
