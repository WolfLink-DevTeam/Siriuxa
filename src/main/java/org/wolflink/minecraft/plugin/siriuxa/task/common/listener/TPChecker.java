package org.wolflink.minecraft.plugin.siriuxa.task.common.listener;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class TPChecker extends WolfirdListener {
    @EventHandler(priority = EventPriority.LOWEST)
    void on(PlayerTeleportEvent event) {
        if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE)) {
            World fromWorld = event.getFrom().getWorld();
            if(fromWorld == null) return;
            if(event.getTo() == null) return;
            World toWorld = event.getTo().getWorld();
            if(toWorld == null) return;
            if(fromWorld.equals(toWorld)) return;
            Notifier.chat("你不能在观察者模式下跨世界传送！",event.getPlayer());
            event.setCancelled(true);
        }
    }
}
