package org.wolflink.minecraft.plugin.siriuxa.task.ornaments.smartai.customgoals;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

/**
 * 透视索敌
 */
public class XrayNearestAttackableTargetGoal<T extends LivingEntity> extends TargetGoal {

    private static final int DEFAULT_RANDOM_INTERVAL = 10;
    protected final Class<T> targetType;
    protected final int randomInterval;
    @Nullable
    protected LivingEntity target;
    protected TargetingConditions targetConditions;

    public XrayNearestAttackableTargetGoal(Mob mob, Class<T> targetClass) {
        this(mob, targetClass, DEFAULT_RANDOM_INTERVAL, false, null);
    }

    public XrayNearestAttackableTargetGoal(Mob mob, Class<T> targetClass, Predicate<LivingEntity> targetPredicate) {
        this(mob, targetClass, DEFAULT_RANDOM_INTERVAL, false, targetPredicate);
    }

    public XrayNearestAttackableTargetGoal(Mob mob, Class<T> targetClass, boolean checkCanNavigate) {
        this(mob, targetClass, DEFAULT_RANDOM_INTERVAL, checkCanNavigate, null);
    }

    public XrayNearestAttackableTargetGoal(Mob mob, Class<T> targetClass, int reciprocalChance, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, false, checkCanNavigate);
        this.targetType = targetClass;
        this.randomInterval = reducedTickDelay(reciprocalChance);
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(targetPredicate);
        this.targetConditions.ignoreLineOfSight();
        if (mob.level().paperConfig().entities.entitiesTargetWithFollowRange)
            this.targetConditions.useFollowRange(); // Paper
    }

    @Override
    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        } else {
            this.findTarget();
            return this.target != null;
        }
    }

    protected AABB getTargetSearchArea(double distance) {
        return this.mob.getBoundingBox().inflate(distance, 4.0D, distance);
    }

    protected void findTarget() {
        if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
            this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), e -> true), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        } else {
            this.target = this.mob.level().getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        }
    }

    @Override
    public void start() {
        this.mob.setTarget(this.target, this.target instanceof ServerPlayer ? org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_PLAYER : org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true); // CraftBukkit - reason
        super.start();
    }

    public void setTarget(@Nullable LivingEntity targetEntity) {
        this.target = targetEntity;
    }
}
