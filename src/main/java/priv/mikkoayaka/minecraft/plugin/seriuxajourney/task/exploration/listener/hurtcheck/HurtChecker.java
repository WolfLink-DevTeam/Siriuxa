package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.listener.hurtcheck;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.TaskRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationTask;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.GameStage;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class HurtChecker extends WolfirdListener {

    /**
     * 启用该监听器的任务类
     */
    private final Set<Class<? extends Task>> availableTaskClasses = new HashSet<>();
    public HurtChecker() {
        availableTaskClasses.add(ExplorationTask.class);
    }
    @Inject
    private TaskRepository taskRepository;
    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if(event.getEntityType() != EntityType.PLAYER)return;
        Player player = (Player) event.getEntity();
        Task task = taskRepository.findByPlayer(player);
        if(task == null) return; // 没有任务
        if(!availableTaskClasses.contains(task.getClass())) return; // 任务模式不可用该检测
        if(!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 任务没在游戏阶段
        if(task.getTaskRegion() == null) return; // 任务区域未设定
        if(player.getWorld() != task.getTaskRegion().getCenter().getWorld()) return; // 不在任务世界

        task.get
    }
}
