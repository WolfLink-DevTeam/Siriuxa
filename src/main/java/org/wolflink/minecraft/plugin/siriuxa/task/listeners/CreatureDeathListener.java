package org.wolflink.minecraft.plugin.siriuxa.task.listeners;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class CreatureDeathListener extends WolfirdListener {
    @Inject
    Config config;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureDeath(EntityDeathEvent e) {
        World world = e.getEntity().getWorld();
        if (!world.getName().equals(config.get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME))) return;
        if (e.getEntity().getKiller() == null) return;

        if (e.getEntity().getType().equals(EntityType.RABBIT)) {
            Rabbit rabbit = (Rabbit) e.getEntity();
            if (rabbit.getRabbitType().equals(Rabbit.Type.THE_KILLER_BUNNY)) {
                Player killer = e.getEntity().getKiller();
                IOC.getBean(VaultAPI.class).addEconomy(killer, 114.514);
                Notifier.chat("击杀彩蛋兔，在兔兔的腹中发现了 §a 114.514" + " §6麦穗。(无需结算，实时到账哦)", killer);
            }
        }
    }
}