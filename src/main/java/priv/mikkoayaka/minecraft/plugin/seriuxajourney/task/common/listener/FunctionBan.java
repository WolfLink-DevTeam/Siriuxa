package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.listener;

import com.sk89q.worldedit.event.platform.BlockInteractEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.TaskRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.GameStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

/**
 * 功能禁用
 */
@Singleton
public class FunctionBan extends WolfirdListener {

    @Inject
    private TaskRepository taskRepository;
    /**
     * 任务过程中禁止使用末影箱
     */
    @EventHandler(priority = EventPriority.LOWEST)
    void banTaskEnderChest(InventoryOpenEvent event) {
        HumanEntity humanEntity = event.getPlayer();
        Task task = taskRepository.findByPlayerUuid(humanEntity.getUniqueId());
        if(task == null) return; // 没在任务中
        if(!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 没在游戏阶段
        if(event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
            Notifier.chat("末影箱空间已经受到了严重感染，千万不要打开它！", (Player) humanEntity);
            event.setCancelled(true);
        }
    }

    /**
     * 任务过程中禁止与村民交互
     */
    @EventHandler(priority = EventPriority.LOWEST)
    void banTaskVillager(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Task task = taskRepository.findByPlayerUuid(player.getUniqueId());
        if(task == null) return; // 没在任务中
        if(!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 没在游戏阶段
        if(event.getRightClicked().getType().equals(EntityType.VILLAGER)) event.setCancelled(true);
    }
}
