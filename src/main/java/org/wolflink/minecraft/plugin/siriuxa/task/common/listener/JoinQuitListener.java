package org.wolflink.minecraft.plugin.siriuxa.task.common.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskService;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class JoinQuitListener extends WolfirdListener {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private TaskService taskService;
    @Inject
    private Config config;

    @EventHandler
    void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        taskService.online(player);
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task == null && player.getWorld().getName().equalsIgnoreCase(config.get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME))) {
            Bukkit.getScheduler().runTaskLater(Siriuxa.getInstance(), () ->
                    player.setGameMode(GameMode.SPECTATOR), 1);
        }
    }

    @EventHandler
    void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task != null) {
            taskService.offline(task, player);
        }
    }
}
