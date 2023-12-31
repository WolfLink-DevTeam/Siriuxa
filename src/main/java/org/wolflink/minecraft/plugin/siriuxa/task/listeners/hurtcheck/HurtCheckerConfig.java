package org.wolflink.minecraft.plugin.siriuxa.task.listeners.hurtcheck;

import lombok.Getter;
import org.bukkit.event.entity.EntityDamageEvent;
import org.wolflink.common.ioc.Singleton;

import java.util.EnumSet;
import java.util.Set;

@Getter
@Singleton
public class HurtCheckerConfig {
    /**
     * 排除的伤害类型(不会触发无敌)
     */
    private final Set<EntityDamageEvent.DamageCause> excludeDamageCause = EnumSet.of(
            EntityDamageEvent.DamageCause.VOID,
            EntityDamageEvent.DamageCause.LAVA,
            EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.WITHER,
            EntityDamageEvent.DamageCause.FREEZE,
            EntityDamageEvent.DamageCause.POISON,
            EntityDamageEvent.DamageCause.SUICIDE,
            EntityDamageEvent.DamageCause.DROWNING,
            EntityDamageEvent.DamageCause.HOT_FLOOR,
            EntityDamageEvent.DamageCause.FIRE_TICK,
            EntityDamageEvent.DamageCause.STARVATION,
            EntityDamageEvent.DamageCause.FALLING_BLOCK
    );
}
