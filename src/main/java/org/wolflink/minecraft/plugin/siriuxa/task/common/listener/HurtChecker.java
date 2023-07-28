package org.wolflink.minecraft.plugin.siriuxa.task.common.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.GameStage;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.common.interfaces.HurtCheckAvailable;

@Singleton
public class HurtChecker extends WolfirdListener {
    @Inject
    private TaskRepository taskRepository;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task == null) return; // 没有任务
        if (!(task instanceof HurtCheckAvailable)) return; // 任务模式不可用该检测
        if (!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 任务没在游戏阶段
        if (task.getTaskRegion() == null) return; // 任务区域未设定
        if (player.getWorld() != task.getTaskRegion().getCenter().getWorld()) return; // 不在任务世界
        // 下调大额伤害
        if(event.getDamage() > 12) event.setDamage(12);
        // 扣除麦穗
        double cost = ((HurtCheckAvailable) task).getHurtWheatCost() * event.getFinalDamage();
        task.takeWheat(cost);
    }
}
