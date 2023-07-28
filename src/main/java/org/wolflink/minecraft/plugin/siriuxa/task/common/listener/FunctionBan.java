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
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.GameStage;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

import java.util.HashSet;
import java.util.Set;

/**
 * 功能禁用
 */
@Singleton
public class FunctionBan extends WolfirdListener {

    private static final Set<String> availableCommandPrefixes = new HashSet<>();

    static {
        availableCommandPrefixes.add("/sx lobby");
        availableCommandPrefixes.add("/sx help");
        availableCommandPrefixes.add("/em");
        availableCommandPrefixes.add("/elitemobs");
        availableCommandPrefixes.add("/ag");
        availableCommandPrefixes.add("/tps");
        availableCommandPrefixes.add("/music");
        availableCommandPrefixes.add("/help");
        availableCommandPrefixes.add("/money");
        availableCommandPrefixes.add("/giveup");
    }

    @Inject
    private TaskRepository taskRepository;

    /**
     * 任务过程中禁止使用末影箱
     */
    @EventHandler(priority = EventPriority.LOWEST)
    void banTaskEnderChest(InventoryOpenEvent event) {
        HumanEntity humanEntity = event.getPlayer();
        if (!(humanEntity instanceof Player)) return; // 不是玩家
        Task task = taskRepository.findByTaskTeamPlayer((Player) humanEntity);
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
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task == null) return; // 没在任务中
        if (!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 没在游戏阶段
        if (event.getRightClicked().getType().equals(EntityType.VILLAGER)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void banTaskCommand(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().isOp()) return; // 不处理管理员
        Player player = event.getPlayer();
        // 在任务世界
        if (player.getWorld().getName().equals(IOC.getBean(Config.class).get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME))) {
            // 允许在任务过程中执行的白名单指令
            for (String prefix : availableCommandPrefixes) {
                if (event.getMessage().startsWith(prefix)) return;
            }
            event.setCancelled(true);
        }
    }
}
