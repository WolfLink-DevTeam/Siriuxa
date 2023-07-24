package org.wolflink.minecraft.plugin.siriuxa.task.common.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.world.SculkSpawnBox;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class SculkSpreader extends WolfirdListener {
    @EventHandler
    void on(EntityDeathEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(),()->{
            // 在探索世界
            if(event.getEntity().getWorld().getName()
                    .equals(IOC.getBean(Config.class).get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME))) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                // 20%几率预生成
                if(random.nextDouble() <= 0.2) {
                    SculkSpawnBox sculkSpawnBox = new SculkSpawnBox(event.getEntity().getLocation().clone().add(0,-2,0));
                    if(sculkSpawnBox.isAvailable()) {
                        Bukkit.getScheduler().runTask(Siriuxa.getInstance(), sculkSpawnBox::spawn);
                    }
                }
            }
        });
    }
}
