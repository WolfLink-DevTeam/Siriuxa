package org.wolflink.minecraft.plugin.siriuxa.task.listeners;


import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskLumenLeftNotifyEvent;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class LumenTip extends WolfirdListener {
    @EventHandler
    void on(TaskLumenLeftNotifyEvent event) {
        switch (event.getStatus()) {
            case FEW -> {
                event.getTask().getTaskPlayers().forEach(player -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                    player.sendTitle("", "§c当前幽匿光体所剩无几，请注意补充！", 8, 24, 8);
                });
            }
            case ENOUGH -> {
                event.getTask().getTaskPlayers().forEach(player -> {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    player.sendTitle("", "§c当前幽匿光体剩余不多！", 8, 24, 8);
                });
            }
            case INSUFFICIENT -> {
                event.getTask().getTaskPlayers().forEach(player -> {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);
                    player.sendTitle("", "§a当前幽匿光体剩余充足。", 8, 24, 8);
                });
            }
        }
    }
}
