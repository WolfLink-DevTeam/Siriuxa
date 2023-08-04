package org.wolflink.minecraft.plugin.siriuxa.task.listeners.hurtcheck;

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

@Singleton
public class HurtChecker extends WolfirdListener {

    @Inject
    private TaskRepository taskRepository;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        if (HurtCheckerConfig.excludeDamageCause.contains(event.getCause())) return;
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
                player.setInvulnerable(false), HurtCheckerConfig.INVULNERABLE_TICKS);
        // 扣除麦穗
        double cost = wheatTask.getHurtWheatCost() * event.getFinalDamage();
        wheatTask.takeWheat(cost);
    }
}
