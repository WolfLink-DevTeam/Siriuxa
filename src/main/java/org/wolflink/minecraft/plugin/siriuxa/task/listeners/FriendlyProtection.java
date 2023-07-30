package org.wolflink.minecraft.plugin.siriuxa.task.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.taskstage.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeamRepository;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class FriendlyProtection extends WolfirdListener {

    @Inject
    private GlobalTeamRepository globalTeamRepository;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return; // 受伤的不是玩家
        if (event.getDamager().getType() != EntityType.PLAYER) return; //攻击者不是玩家
        Player a = (Player) event.getEntity();
        GlobalTeam aGlobalTeam = globalTeamRepository.findByPlayer(a);
        if (aGlobalTeam == null) return; // 没在队伍中
        Player b = (Player) event.getDamager();
        GlobalTeam bGlobalTeam = globalTeamRepository.findByPlayer(b);
        if (bGlobalTeam == null) return;
        if (aGlobalTeam != bGlobalTeam) return; // 不在同一个队伍
        Task task = aGlobalTeam.getSelectedTask();
        if (task == null) return; // 没选择任务
        if (!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 没在游戏阶段
        TaskDifficulty taskDifficulty = task.getTaskDifficulty();
        int level = taskDifficulty.getLevel();
        if (level <= 2) {
            Notifier.chat("§c你的队友受到了某种神秘力量的保护，你无法伤害Ta。", b);
            event.setCancelled(true);
        }
    }
}
