package org.wolflink.minecraft.plugin.siriuxa.task.common.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.ReadyStage;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;

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
        if (task == null) return; // 没在任务中
        if (!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 没在游戏阶段
        if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
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
        if (task == null) return; // 没在任务中
        if (!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 没在游戏阶段
        if (event.getRightClicked().getType().equals(EntityType.VILLAGER)) event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    void banTaskCommand(PlayerCommandPreprocessEvent event) {
        if(event.getPlayer().isOp()) return; // 不处理管理员
        Player player = event.getPlayer();
        Task task = taskRepository.findByPlayerUuid(player.getUniqueId());
        if (task == null) return; // 没在任务中
        Stage stage = task.getStageHolder().getThisStage();
        if (!(stage instanceof GameStage) && !(stage instanceof ReadyStage)) return; // 没在游戏阶段/准备阶段
        event.setCancelled(true);
    }
}
