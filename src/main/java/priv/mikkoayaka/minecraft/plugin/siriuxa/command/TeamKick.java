package priv.mikkoayaka.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;
import priv.mikkoayaka.minecraft.plugin.siriuxa.team.TaskTeamService;

@Singleton
public class TeamKick extends WolfirdCommand {
    public TeamKick() {
        super(false, false, true, "sj team kick {player}", "踢出指定玩家。");
    }

    @Inject
    private TaskTeamService taskTeamService;

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        taskTeamService.kickPlayer(player, strings[0]).show(player);
    }
}
