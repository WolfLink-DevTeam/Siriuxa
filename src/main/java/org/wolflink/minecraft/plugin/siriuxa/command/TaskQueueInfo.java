package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.ExplorationTaskQueue;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

import java.util.Calendar;

@Singleton
public class TaskQueueInfo extends WolfirdCommand {
    @Inject
    private ExplorationTaskQueue explorationTaskQueue;
    public TaskQueueInfo() {
        super(false, false, true, "sx task queue", "查看当前任务队列状态");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        Notifier.chat("§6服务器队列信息",player);
        int secs = (int) ((Calendar.getInstance().getTimeInMillis() - explorationTaskQueue.getLastStarted().getTimeInMillis()) / 1000);
        player.sendMessage(" ");
        player.sendMessage("§a距离最近一组开始的任务：§f"+secs+"秒");
        player.sendMessage("§a进行中：§f"+explorationTaskQueue.getNowSize()+"组");
        player.sendMessage("§a最大容量：§f"+explorationTaskQueue.getMAX_SIZE()+"组");
        player.sendMessage(" ");
    }
}
