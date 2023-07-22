package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;
import org.wolflink.minecraft.plugin.siriuxa.team.TeamService;

@Singleton
public class TeamCreate extends WolfirdCommand {

    @Inject
    private TeamService teamService;

    public TeamCreate() {
        super(false, false, true, "sx team create", "创建一个小队");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        teamService.create(player).show(player);
    }
}
