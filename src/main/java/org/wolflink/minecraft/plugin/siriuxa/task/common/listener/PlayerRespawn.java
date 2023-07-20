package org.wolflink.minecraft.plugin.siriuxa.task.common.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class PlayerRespawn extends WolfirdListener {
    /**
     * 玩家在任务世界死亡只会复活在任务世界
     */
    @EventHandler(priority = EventPriority.MONITOR)
    void on(PlayerRespawnEvent event) {
        World world = event.getPlayer().getWorld();
        event.setRespawnLocation(new Location(world,0,world.getHighestBlockYAt(0,0),0));
    }
}
