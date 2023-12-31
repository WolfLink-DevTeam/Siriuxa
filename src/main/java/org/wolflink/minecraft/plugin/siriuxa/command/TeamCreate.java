package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeamService;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class TeamCreate extends WolfirdCommand {

    @Inject
    private GlobalTeamService globalTeamService;

    public TeamCreate() {
        super(false, false, true, "sx team create", "创建一个小队");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        globalTeamService.create(player).show(player);
    }
}
