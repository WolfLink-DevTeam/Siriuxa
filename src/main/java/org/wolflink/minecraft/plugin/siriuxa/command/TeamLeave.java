package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeamService;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class TeamLeave extends WolfirdCommand {
    public TeamLeave() {
        super(false, false, true, "sx team leave", "离开当前所在队伍。");
    }

    @Inject
    private TaskTeamService taskTeamService;

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        taskTeamService.leaveTeam(player).show(player);
    }
}
