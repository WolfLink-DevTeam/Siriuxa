package org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.taskstage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskStage;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
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
        getSubScheduler().runTaskTimer(() -> {
            Set<UUID> readyPlayers = new HashSet<>();
            for (Player player : Objects.requireNonNull(readyLoc.getWorld())
                    .getNearbyEntities(readyLoc, radius, radius, radius, entity -> entity.getType() == EntityType.PLAYER)
                    .stream().map(Player.class::cast).collect(Collectors.toSet())) {
                if (task.getGlobalTeam().contains(player) && (player.getLocation().clone().getBlock().getType().equals(Material.END_PORTAL_FRAME))) {
                    readyPlayers.add(player.getUniqueId());
                }
            }
            if (readyPlayers.size() == task.getGlobalTeam().size()) {
                Bukkit.getScheduler().runTask(Siriuxa.getInstance(), () -> {
                    Result result = IOC.getBean(TaskService.class).ready(task);
                    task.getGlobalTeam().getPlayers().forEach(result::show);
                });
            }
        }, 20, 20);
    }
}
