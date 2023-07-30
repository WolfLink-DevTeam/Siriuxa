package org.wolflink.minecraft.plugin.siriuxa.task.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class PlayerRespawn extends WolfirdListener {
    /**
     * 玩家在任务世界死亡只会复活在任务世界
     */
    @EventHandler(priority = EventPriority.MONITOR)
    void on(PlayerRespawnEvent event) {
        World world = event.getPlayer().getWorld();
        Config config = IOC.getBean(Config.class);
        if (world.getName().equalsIgnoreCase(config.get(ConfigProjection.LOBBY_WORLD_NAME))) {
            event.setRespawnLocation(config.getLobbyLocation());
        } else event.setRespawnLocation(new Location(world, 0, world.getHighestBlockYAt(0, 0), 0));
    }
}
