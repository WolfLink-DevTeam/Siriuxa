package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.loot.LootService;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

/**
 * 在服务器中动态修改怪物掉落战利品的概率
 */
@Singleton
public class AddLoot extends WolfirdCommand {
    @Inject
    private LootService lootService;

    public AddLoot() {
        super(true, false, false, "sx addloot {entityType} {dropChance}", "动态修改怪物掉落战利品的概率");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        lootService.addLoot(player, strings[0], strings[1]).show(player);
    }
}
