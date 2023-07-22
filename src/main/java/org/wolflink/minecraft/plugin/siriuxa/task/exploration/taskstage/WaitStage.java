package org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.common.stage.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.common.stage.TaskStage;

import java.util.*;
import java.util.stream.Collectors;

public class WaitStage extends TaskStage {
    public WaitStage(TaskLinearStageHolder stageHolder) {
        super("等待中", stageHolder);
    }
    @Override
    protected void onEnter() {
        super.onEnter();
        Config config = IOC.getBean(Config.class);
        Location readyLoc = config.getReadyLocation();
        int radius = config.get(ConfigProjection.LOBBY_READY_RADIUS);
        Task task = getStageHolder().getTask();
        runTaskTimer(()->{
            Set<UUID> readyPlayers = new HashSet<>();
            for (Player player : Objects.requireNonNull(readyLoc.getWorld())
                    .getNearbyEntities(readyLoc,radius,radius,radius, entity -> entity.getType() == EntityType.PLAYER)
                    .stream().map(entity -> (Player) entity).collect(Collectors.toSet())) {
                if(task.getGlobalTeam().contains(player)) {
                    if(player.getLocation().clone().getBlock().getType().equals(Material.END_PORTAL_FRAME)) {
                        readyPlayers.add(player.getUniqueId());
                    }
                }
            }
            if(readyPlayers.size() == task.getGlobalTeam().size()) {
                Bukkit.getScheduler().runTask(Siriuxa.getInstance(),()->{
                    IOC.getBean(TaskService.class).ready(task);
                });
            }
        },20,20);
    }
}
