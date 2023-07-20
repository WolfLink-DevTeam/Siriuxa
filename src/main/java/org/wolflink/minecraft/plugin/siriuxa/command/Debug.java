package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTables;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.InvBackupService;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRepository;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class Debug extends WolfirdCommand {
    @Inject
    private TaskRepository taskRepository;

    public Debug() {
        super(true, false, true, "sx debug {loot}", "开发者调试指令");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        Location location = player.getLocation().clone().add(0,-1,0);
        location.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) location.getBlock().getState();
        try {
            chest.setLootTable(LootTables.valueOf(strings[0]).getLootTable());
            chest.update();
        } catch (Exception ignore) {}
    }
}
