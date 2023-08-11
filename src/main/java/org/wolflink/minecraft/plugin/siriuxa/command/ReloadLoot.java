package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.loot.LootService;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

/**
 * 重载有关于自定义战利品的配置文件
 */
@Singleton
public class ReloadLoot extends WolfirdCommand {
    @Inject
    private LootService lootService;

    public ReloadLoot() {
        super(true, false, true, "sx reloadloot", "重载有关于自定义战利品的配置文件");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        lootService.reloadLoot();
    }
}
