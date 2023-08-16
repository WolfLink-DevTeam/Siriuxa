package org.wolflink.minecraft.plugin.siriuxa.task.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.command.GoLobby;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

/**
 * 传送相关的检查
 */
@Singleton
public class TPChecker extends WolfirdListener {
    @EventHandler(priority = EventPriority.LOWEST)
    void on(PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE)) {
            World fromWorld = event.getFrom().getWorld();
            if (fromWorld == null) return;
            if (event.getTo() == null) return;
            World toWorld = event.getTo().getWorld();
            if (toWorld == null) return;
            if (fromWorld.equals(toWorld)) return;
            Notifier.chat("你不能在观察者模式下跨世界传送！", event.getPlayer());
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {
        String taskWorldName = IOC.getBean(Config.class).get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME);
        getSubScheduler().runTaskTimerAsync(()->{
            for (Player player : Bukkit.getOnlinePlayers()) {
                // 玩家在任务世界
                if(player.getWorld().getName().equals(taskWorldName)) {
                    // Y >= 300 而且是观察者
                    if(player.getLocation().getBlockY() >= 300 && player.getGameMode() == GameMode.SPECTATOR) {
                        getSubScheduler().runTask(()->IOC.getBean(GoLobby.class).execute(player));
                    }
                }
            }
        },20,20);
    }
}
