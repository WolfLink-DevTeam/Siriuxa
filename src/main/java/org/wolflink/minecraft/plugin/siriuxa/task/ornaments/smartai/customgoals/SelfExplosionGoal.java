package org.wolflink.minecraft.plugin.siriuxa.task.ornaments.smartai.customgoals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

/**
 * 苦力怕英勇自爆目标，靠近目标一定距离无视掩体爆炸
 */
public class SelfExplosionGoal<T extends LivingEntity> extends XrayNearestAttackableTargetGoal<T> {
    private static final int SELF_EXPLOSION_RADIUS = 6;
    private final Creeper creeper;

    public SelfExplosionGoal(Creeper mob, Class<T> targetClass) {
        super(mob, targetClass);
        creeper = mob;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (creeper.getTarget() != null && creeper.distanceTo(creeper.getTarget()) <= SELF_EXPLOSION_RADIUS) {
            creeper.ignite();
        }
    }
}
