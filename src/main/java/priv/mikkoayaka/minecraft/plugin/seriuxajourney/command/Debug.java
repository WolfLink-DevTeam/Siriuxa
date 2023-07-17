package priv.mikkoayaka.minecraft.plugin.seriuxajourney.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.invbackup.InvBackupService;

@Singleton
public class Debug extends WolfirdCommand {
    @Inject
    private InvBackupService invBackupService;

    public Debug() {
        super(true, false, true, "sj debug", "开发者调试指令");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        invBackupService.saveMainInv(player).show(player);
        Bukkit.getScheduler().runTaskLater(SeriuxaJourney.getInstance(), () -> {
            invBackupService.loadMainInv(player).show(player);
        }, 20 * 5);
    }
}
