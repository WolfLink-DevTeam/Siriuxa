package priv.mikkoayaka.minecraft.plugin.seriuxajourney.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class TeamDeny extends WolfirdCommand {
    @Inject
    private TeamInvite teamInvite;
    public TeamDeny() {
        super(false, false, true, "sj team deny", "拒绝队伍邀请");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        teamInvite.deny((Player) commandSender);
    }
}
