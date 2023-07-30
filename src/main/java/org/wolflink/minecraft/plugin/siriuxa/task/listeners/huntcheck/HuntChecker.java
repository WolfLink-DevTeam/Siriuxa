package org.wolflink.minecraft.plugin.siriuxa.task.listeners.huntcheck;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.file.Lang;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.WheatTask;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.taskstage.GameStage;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class HuntChecker extends WolfirdListener {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private HuntValues huntValues;
    @Inject
    private Lang lang;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMonsterDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Monster)) return; // 不是怪物被击杀
        Player player = e.getEntity().getKiller();
        if (player == null) return; // 与玩家无关
        if (e.getEntity().hasMetadata("bySpawner")) return; // 来自刷怪笼
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task == null) return; // 没有任务
        if(!(task instanceof WheatTask wheatTask)) return; // 不是麦穗任务，无法应用
        if (!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 任务没在游戏阶段
        if (task.getTaskArea() == null) return; // 任务区域未设定
        if (player.getWorld() != task.getTaskArea().getCenter().getWorld()) return; // 不在任务世界
        EntityType entityType = e.getEntityType();
        if (!huntValues.getMonsterTypes().contains(entityType)) return;
        huntValues.doRecord(entityType);
        double wheatValue = huntValues.getHuntValue(entityType);
        wheatTask.addWheat(wheatValue);
        for (Player taskPlayer : task.getTaskPlayers()) {
            taskPlayer.playSound(taskPlayer.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_FALL, 1, 2f);
            //TODO 改为 Hologram 提示
            taskPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent("§f" + player.getName() + " §7刚刚击杀了 " + lang.get("monster." + entityType.name().toLowerCase(), "未知怪物") + " §7获得 §f" + String.format("%.1f", wheatValue) + " §6麦穗"));
        }
    }
}
