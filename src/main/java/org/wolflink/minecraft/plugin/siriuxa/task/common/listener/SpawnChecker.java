package org.wolflink.minecraft.plugin.siriuxa.task.common.listener;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class SpawnChecker extends WolfirdListener {
    @EventHandler
    void on(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(player.getWorld().getName().equals("plot")) {
            if(player.getLocation().add(0,-1,0).getBlock().getType().equals(Material.BEACON)) {
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,1f,1f);
                player.teleport(IOC.getBean(Config.class).getLobbyLocation());
            }
        }
    }
}
