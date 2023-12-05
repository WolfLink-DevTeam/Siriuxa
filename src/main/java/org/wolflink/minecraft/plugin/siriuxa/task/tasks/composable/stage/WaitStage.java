package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.stage;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.BaseWaitStage;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskQueue;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WaitStage extends BaseWaitStage {
    public WaitStage(TaskLinearStageHolder stageHolder) {
        super(stageHolder);
    }

    /**
     * 检测玩家是否全部准备就绪
     */
    @Override
    @NonNull
    protected Supplier<Boolean> nextCondition() {
        Config config = IOC.getBean(Config.class);
        Location readyLoc = config.getReadyLocation();
        int radius = config.get(ConfigProjection.LOBBY_READY_RADIUS);
        Task task = getStageHolder().getTask();
        TaskQueue taskQueue = IOC.getBean(TaskQueue.class);
        return () -> {
            Set<UUID> readyPlayers = new HashSet<>();
            for (Player player : Objects.requireNonNull(readyLoc.getWorld())
                    .getNearbyEntities(readyLoc, radius, radius, radius, entity -> entity.getType() == EntityType.PLAYER)
                    .stream().map(Player.class::cast).collect(Collectors.toSet())) {
                if (task.getGlobalTeam().contains(player) && (player.getLocation().clone().getBlock().getType().equals(Material.END_PORTAL_FRAME))) {
                    readyPlayers.add(player.getUniqueId());
                }
            }
            boolean readyStatus = readyPlayers.size() == task.getGlobalTeam().size();
            if(readyStatus) {
                Result isBlocking = taskQueue.isBlocking();
                if (isBlocking.result()) return false;
                taskQueue.taskStarted();
                return true;
            }
            else return false;
        };
    }

    @Override
    @NonNull
    protected Long nextCheckPeriodTick() {
        return 20 * 3L;
    }

    @Override
    protected void initScheduleTask() {

    }

    @Override
    protected void onEnter() {

    }

    @Override
    protected void onLeave() {

    }
}
