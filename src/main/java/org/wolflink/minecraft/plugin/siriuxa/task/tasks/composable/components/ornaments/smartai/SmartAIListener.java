package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.smartai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.monster.TaskSpawnEntityEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.ComposableTask;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.OrnamentType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.smartai.customgoals.SelfExplosionGoal;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.smartai.customgoals.XrayNearestAttackableTargetGoal;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.smartai.customgoals.ZombieBlockGoal;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

/**
 * 更聪明的怪物AI
 */
@Singleton
public class SmartAIListener extends WolfirdListener {
    @EventHandler
    public void on(TaskSpawnEntityEvent event) {
        Task task = event.getTask();
        if(!(task instanceof ComposableTask composableTask)) return;
        if (composableTask.getOrnamentTypes().contains(OrnamentType.SMART_AI)) {
            Entity craftEntity = ((CraftEntity) event.getEntity()).getHandle();
            if (craftEntity instanceof Mob mob) {
                addXrayAbility(mob);
            }
            if (craftEntity instanceof Zombie zombie) {
                enhanceZombie(zombie);
            } else if (craftEntity instanceof Creeper creeper) {
                enhanceCreeper(creeper);
            }
        }
    }

    private void enhanceZombie(Zombie zombie) {
        zombie.goalSelector.addGoal(1, new ZombieBlockGoal(zombie));
    }

    private void addXrayAbility(Mob mob) {
        mob.targetSelector.addGoal(1, new XrayNearestAttackableTargetGoal<>(mob, Player.class));
    }

    private void enhanceCreeper(Creeper creeper) {
        creeper.goalSelector.addGoal(1, new SelfExplosionGoal<>(creeper, Player.class));
    }
}
