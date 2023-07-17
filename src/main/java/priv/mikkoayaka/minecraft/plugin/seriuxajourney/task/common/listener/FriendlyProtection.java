package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.team.TaskTeam;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.team.TaskTeamRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

@Singleton
public class FriendlyProtection extends WolfirdListener {

    @Inject
    private TaskTeamRepository taskTeamRepository;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void on(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return; // 受伤的不是玩家
        if (event.getDamager().getType() != EntityType.PLAYER) return; //攻击者不是玩家
        Player a = (Player) event.getEntity();
        TaskTeam aTeam = taskTeamRepository.findByPlayer(a);
        if (aTeam == null) return;
        Player b = (Player) event.getDamager();
        TaskTeam bTeam = taskTeamRepository.findByPlayer(b);
        if (bTeam == null) return;
        if (aTeam != bTeam) return; // 不在同一个队伍
        Task task = aTeam.getSelectedTask();
        if (task == null) return; // 没选择任务
        TaskDifficulty taskDifficulty = task.getTaskDifficulty();
        int level = taskDifficulty.getLevel();
        if (level <= 2) {
            event.setCancelled(true);
            Notifier.chat("§c你的队友受到了某种神秘力量的保护，你无法伤害Ta。", b);
        }
    }
}
