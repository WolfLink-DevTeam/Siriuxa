package priv.mikkoayaka.minecraft.plugin.seriuxajourney.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.team.TaskTeam;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.team.TaskTeamRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

public class TeamInfo extends WolfirdCommand {
    public TeamInfo() {
        super(false, false, true, "sj team", "查看队伍详细信息");
    }
    @Inject
    private TaskTeamRepository taskTeamRepository;

    /**
     * 当前任务：
     *
     * 自由勘探 困难 等待中
     *
     * 成员列表：
     */
    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        TaskTeam taskTeam = taskTeamRepository.findByPlayer(player);
        if(taskTeam == null) {
            Notifier.chat("§e你没有处在队伍中。",player);
            return;
        }
        String taskInfo = "§r\n队伍任务：\n§r\n";
        Task task = taskTeam.getSelectedTask();
        if(task == null) taskInfo += "§7暂未选择\n§r\n";
        else {
            taskInfo += task.getColor()+task.getName()
                    +" "+task.getTaskDifficulty().getColor()+task.getTaskDifficulty().getName()
                    +" §f"+task.getStageHolder().getThisStage().getDisplayName()
                    +"\n§r\n";
        }
        StringBuilder msg = new StringBuilder("成员列表：\n§r\n");
        for (OfflinePlayer offlinePlayer : taskTeam.getOfflinePlayers()) {
            String color;
            if(offlinePlayer.isOnline()) color = "§a(在线) ";
            else color = "§c(离线) ";
            msg.append(color).append(offlinePlayer.getName()).append("\n§r\n");
        }

        Notifier.notify(taskInfo+msg,player);
    }
}
