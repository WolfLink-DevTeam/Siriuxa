package org.wolflink.minecraft.plugin.siriuxa.task.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.WheatTask;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.taskstage.GameStage;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

import java.util.ArrayList;

@Singleton
public class HurtChecker extends WolfirdListener {
    private static final long INVULNERABLE_TICKS = 21;
    private static final ArrayList<EntityDamageEvent.DamageCause> excludeDamageCause = new ArrayList<>();

    static {
        excludeDamageCause.add(EntityDamageEvent.DamageCause.KILL);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.VOID);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.LAVA);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.FIRE);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.WITHER);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.FREEZE);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.POISON);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.SUICIDE);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.DROWNING);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.HOT_FLOOR);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.FIRE_TICK);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.STARVATION);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.PROJECTILE);
        excludeDamageCause.add(EntityDamageEvent.DamageCause.FALLING_BLOCK);
    }

    @Inject
    private TaskRepository taskRepository;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        if (excludeDamageCause.contains(event.getCause())) return;
        Player player = (Player) event.getEntity();
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task == null) return; // 没有任务
        if (!(task instanceof WheatTask wheatTask)) return; // 任务模式不可用该检测
        if (!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 任务没在游戏阶段
        if (task.getTaskArea() == null) return; // 任务区域未设定
        if (player.getWorld() != task.getTaskArea().getCenter().getWorld()) return; // 不在任务世界
        // 下调大额伤害
        if (event.getDamage() > 12) event.setDamage(12);
        // 指定时间内只会受到一次伤害
        player.setInvulnerable(true);
        Bukkit.getScheduler().runTaskLater(Siriuxa.getInstance(), () ->
                player.setInvulnerable(false), INVULNERABLE_TICKS);
        // 扣除麦穗
        double cost = wheatTask.getHurtWheatCost() * event.getFinalDamage();
        wheatTask.takeWheat(cost);
    }
}
