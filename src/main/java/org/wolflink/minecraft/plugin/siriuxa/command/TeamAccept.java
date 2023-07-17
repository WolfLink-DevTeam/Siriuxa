package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class TeamAccept extends WolfirdCommand {
    @Inject
    private TeamInvite teamInvite;

    public TeamAccept() {
        super(false, false, true, "sx team accept", "接受队伍邀请");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        teamInvite.accept((Player) commandSender);
    }
}
