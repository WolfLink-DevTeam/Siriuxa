package org.wolflink.minecraft.plugin.siriuxa.task.common.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
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
    @EventHandler
    void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        taskService.online(player);
    }
    @EventHandler
    void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Task task = taskRepository.findByPlayer(player);
        if(task != null) {
            taskService.offline(task,player);
        }
    }
}
