package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeamRepository;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class TeamInfo extends WolfirdCommand {
    @Inject
    private GlobalTeamRepository globalTeamRepository;

    public TeamInfo() {
        super(false, false, true, "sx team", "查看队伍详细信息");
    }

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
        GlobalTeam globalTeam = globalTeamRepository.findByPlayer(player);
        if (globalTeam == null) {
            Notifier.chat("§e你没有处在队伍中。", player);
            return;
        }
        String taskInfo = "§r\n队伍任务：\n§r\n";
        Task task = globalTeam.getSelectedTask();
        if (task == null) taskInfo += "§7暂未选择\n§r\n";
        else {
            taskInfo += task.getColor() + task.getName()
                    + " " + task.getTaskDifficulty().getColor() + task.getTaskDifficulty().getName()
                    + " §f" + task.getStageHolder().getThisStage().getDisplayName()
                    + "\n§r\n";
        }
        String ownerInfo = "队长：§6"+globalTeam.getOfflineOwner().getName()+"\n§r\n";
        StringBuilder msg = new StringBuilder("成员列表：\n§r\n");
        for (OfflinePlayer offlinePlayer : globalTeam.getOfflinePlayers()) {
            String color;
            if (offlinePlayer.isOnline()) color = "§a(在线) ";
            else color = "§c(离线) ";
            msg.append(color).append(offlinePlayer.getName()).append("\n");
        }
        msg.append("\n§r");
        Notifier.notify(taskInfo + ownerInfo + msg, player);
    }
}
