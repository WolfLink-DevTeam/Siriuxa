package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeamService;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class TeamGiveUpTask extends WolfirdCommand {
    public TeamGiveUpTask() {
        super(false, false, true, "sx team giveuptask", "放弃队伍所选的任务，如果任务在进行中则会直接失败。");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        IOC.getBean(GlobalTeamService.class).giveUpTask(player).show(player);
    }
}
