package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeamService;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class TeamLeave extends WolfirdCommand {
    @Inject
    private GlobalTeamService globalTeamService;

    public TeamLeave() {
        super(false, false, true, "sx team leave", "离开当前所在队伍。");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        globalTeamService.leave(player).show(player);
    }
}
