package org.wolflink.minecraft.plugin.siriuxa.task.listeners;

import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class CreatureSpawnListener extends WolfirdListener {
    @Inject
    Config config;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        World world = e.getEntity().getWorld();
        if (!world.getName().equals(config.get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME))) return;
        if (!(e.getEntity() instanceof Monster)) return;

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL || e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.DEFAULT) {
            e.setCancelled(true);
        }

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            e.getEntity().setMetadata("bySpawner", new FixedMetadataValue(Siriuxa.getInstance(), true));
        }
    }
}
