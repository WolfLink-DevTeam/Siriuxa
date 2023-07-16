package priv.mikkoayaka.minecraft.plugin.seriuxajourney.monster.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

/**
 * 怪物相关事件监听器
 */
@Singleton
public class MonsterListener extends WolfirdListener {
    @EventHandler
    void on(EntitySpawnEvent event) {
        // TODO
    }
}
