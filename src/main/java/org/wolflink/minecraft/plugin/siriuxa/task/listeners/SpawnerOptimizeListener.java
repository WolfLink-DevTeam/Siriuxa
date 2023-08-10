package org.wolflink.minecraft.plugin.siriuxa.task.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class SpawnerOptimizeListener extends WolfirdListener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawnerSpawn(SpawnerSpawnEvent event) {
        CreatureSpawner spawner = event.getSpawner();
        Block spawnerBlock = spawner.getBlock();
        Bukkit.getScheduler().runTaskLater(Siriuxa.getInstance(), () -> {
            spawnerBlock.setType(Material.AIR);
        }, 20L * 60 * 10);
        if (spawner.getRequiredPlayerRange() <= 4) return;
        spawner.setSpawnCount(1);
        spawner.setSpawnRange(2);
        spawner.setMinSpawnDelay(200);
        spawner.setMaxSpawnDelay(200);
        spawner.setRequiredPlayerRange(4);
        spawner.update();
    }
}
