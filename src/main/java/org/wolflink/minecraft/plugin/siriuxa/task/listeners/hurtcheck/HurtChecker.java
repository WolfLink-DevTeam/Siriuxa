package org.wolflink.minecraft.plugin.siriuxa.task.listeners.hurtcheck;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskAttributeType;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.BaseGameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.ComposableTask;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class HurtChecker extends WolfirdListener {

    @Inject
    private TaskRepository taskRepository;
    @Inject
    private HurtCheckerConfig hurtCheckerConfig;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        if (hurtCheckerConfig.getExcludeDamageCause().contains(event.getCause())) return;
        Player player = (Player) event.getEntity();
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task == null) return; // 没有任务
        if (!(task instanceof ComposableTask lumenTask)) return; // 任务模式不可用该检测
        if (!(task.getStageHolder().getThisStage() instanceof BaseGameStage)) return; // 任务没在游戏阶段
        if (task.getTaskRegion().getTaskArea() == null) return; // 任务区域未设定
        if (player.getWorld() != task.getTaskRegion().getTaskArea().getCenter().getWorld()) return; // 不在任务世界
        // 下调大额伤害
        if (event.getDamage() > 12) event.setDamage(12);
        // 上调小额伤害
        if (player.getHealth() >= 3 && event.getDamage() < 2) event.setDamage(2);
        // 扣除麦穗
        double cost = lumenTask.getTaskAttribute().getAttribute(TaskAttributeType.HURT_LUMEN_COST,0.0) * event.getFinalDamage();
        double lumen = lumenTask.getTaskAttribute().getAttribute(TaskAttributeType.LUMEN_LEFT,0.0);
        lumenTask.getTaskAttribute().setAttribute(TaskAttributeType.LUMEN_LEFT,lumen - cost);
    }
}
