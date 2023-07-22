package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeam;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeamRepository;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;

@Singleton
public class TeamInfo extends WolfirdCommand {
    public TeamInfo() {
        super(false, false, true, "sx team", "查看队伍详细信息");
    }

    @Inject
    private TaskTeamRepository taskTeamRepository;

    /**
     * 当前任务：
     * <p>
     * 自由勘探 困难 等待中
     * <p>
     * 成员列表：
     */
    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        TaskTeam taskTeam = taskTeamRepository.findByPlayer(player);
        if (taskTeam == null) {
            Notifier.chat("§e你没有处在队伍中。", player);
            return;
        }
        String taskInfo = "§r\n队伍任务：\n§r\n";
        Task task = taskTeam.getSelectedTask();
        if (task == null) taskInfo += "§7暂未选择\n§r\n";
        else {
            taskInfo += task.getColor() + task.getName()
                    + " " + task.getTaskDifficulty().getColor() + task.getTaskDifficulty().getName()
                    + " §f" + task.getStageHolder().getThisStage().getDisplayName()
                    + "\n§r\n";
        }
        StringBuilder msg = new StringBuilder("成员列表：\n§r\n");
        for (OfflinePlayer offlinePlayer : taskTeam.getOfflinePlayers()) {
            String color;
            if (offlinePlayer.isOnline()) color = "§a(在线) ";
            else color = "§c(离线) ";
            msg.append(color).append(offlinePlayer.getName()).append("\n");
        }
        msg.append("\n§r");
        Notifier.notify(taskInfo + msg, player);
    }
}
