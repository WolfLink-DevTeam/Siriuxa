package org.wolflink.minecraft.plugin.siriuxa.task.common.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.GameStage;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.StageHolder;

@Singleton
public class DeathDuringTask extends WolfirdListener {
    @Inject
    private TaskRepository taskRepository;
    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    void on(EntityDamageEvent event) {
        if(event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        // 没有触发死亡
        if(player.getHealth() - event.getFinalDamage() > 0) return;
        Task task = taskRepository.findByPlayer(player);
        // 没有开始任务
        if(task == null) return;
        // 任务不处在游戏阶段
        if(!(task.getStageHolder().getThisStage() instanceof GameStage)) return;
        event.setCancelled(true);
        task.death(player);
    }
}
