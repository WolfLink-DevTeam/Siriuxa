package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeamService;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class TeamKick extends WolfirdCommand {
    @Inject
    private GlobalTeamService globalTeamService;

    public TeamKick() {
        super(false, false, true, "sx team kick {player}", "踢出指定玩家。");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        globalTeamService.kick(player, strings[0]).show(player);
//        globalTeamService.kick(player, strings[0]).show(player);
    }
}
