package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.impl;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.world.BlockAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationCommandSender;
import org.wolflink.minecraft.plugin.siriuxa.api.world.RegionAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.world.WorldEditAPI;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.plugin.siriuxa.loot.ChestLoot;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskEndEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskStartEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.interfaces.IPreloadable;
import org.wolflink.minecraft.plugin.siriuxa.task.regions.SquareArea;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskLifeCycle;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.ComposableTask;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeam;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ComposableTaskLifeCycle extends TaskLifeCycle implements IPreloadable {

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final ComposableTask task;
    public ComposableTaskLifeCycle(ComposableTask task) {
        this.task = task;
    }
    private boolean isFinished = false;
    private boolean isFailed = false;
    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public boolean isFailed() {
        return isFailed;
    }

    @Override
    public boolean isTaskOver() {
        return isFailed || isFinished;
    }

    @Override
    public void triggerFailed() {
        task.getTaskPlayers().forEach(player -> task.getTaskRecorder().fillRecord(player, false));
        task.getStageHolder().next();
        task.getTaskStat().disable();
        task.stopCheck();
        task.getTaskRecorder().finishRecord();
        failed();
        for (Player player : task.getGlobalTeam().getPlayers()) {
            IOC.getBean(TaskService.class).goLobby(player);
            Siriuxa.getInstance().getSubScheduler().runTaskLater(() -> {
                player.sendTitle("§c任务失败", "§7嘿！别灰心丧气的，下次加油！", 10, 80, 10);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.8f);
            }, 20 * 3L);
        }
        task.deleteTask();
    }

    protected void triggerFinish() {
        triggerFinish(false);
    }
    @Override
    public void triggerFinish(boolean isServerClosing) {
        task.getTaskPlayers().forEach(player -> task.getTaskRecorder().fillRecord(player, true));
        // TODO 不应该操作 Task 的 StageHolder
        task.getStageHolder().next();
        task.getTaskStat().disable();
        task.stopCheck();
        task.getTaskRecorder().finishRecord();
        finish();
        for (Player player : task.getGlobalTeam().getPlayers()) {
            IOC.getBean(TaskService.class).goLobby(player);
            if (!isServerClosing) {
                Siriuxa.getInstance().getSubScheduler().runTaskLater(() -> {
                    player.sendTitle("§a任务完成", "§7前往领取本次任务的报酬吧", 10, 80, 10);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
                    player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
                }, 20 * 3L);
            }
        }
        task.deleteTask();
    }

    @Override
    public void failedCheck() {

    }

    @Override
    public void finishedCheck() {
        ComposableTaskRegion region = (ComposableTaskRegion) task.getTaskRegion();
        task.getSubScheduler().runTaskTimer(() -> {
            if (region.getEvacuablePlayers().size() == task.getTaskTeamSize()) {
                triggerFinish();
            }
        }, 20, 20);
    }

    @Override
    public void start() {
        if (task.getTaskRegion().notAvailable()) {
            Notifier.error("在任务区域未初始化时执行了任务的start方法");
            return;
        }
        task.getTaskRecorder().initRecord();
        task.getTaskStat().enable();
        task.getStrategyDecider().enable();
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {
            Bukkit.getScheduler().runTask(Siriuxa.getInstance(), () -> {
                for (Player player : task.getTaskPlayers()) {
                    IOC.getBean(TaskService.class).goTask(player, task);
                }
                failedCheck();
                finishedCheck();
                ComposableTaskRegion region = (ComposableTaskRegion) task.getTaskRegion();
                region.startEvacuateTask(random.nextInt(12, 20));
                task.getTaskRegion().getTaskArea().startCheck();
            });
            Bukkit.getScheduler().runTask(Siriuxa.getInstance(), () -> Bukkit.getPluginManager().callEvent(new TaskStartEvent(task)));
        });
    }

    @Override
    public void failed() {
        Bukkit.getPluginManager().callEvent(new TaskEndEvent(task, false));
    }

    @Override
    public void finish() {
        Bukkit.getPluginManager().callEvent(new TaskEndEvent(task, true));
    }

    private boolean finishPreLoad = false;
    @Override
    public void preload() {
        task.setTaskTeam(new TaskTeam(task.getGlobalTeam()));
        String worldName = IOC.getBean(Config.class).get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME);
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Notifier.error(worldName + "世界不存在！请检查配置文件");
            return;
        }
        Location regionCenter = IOC.getBean(RegionAPI.class).autoGetRegionCenter(world);
        task.getTaskRegion().setTaskArea(new SquareArea(task, regionCenter));
        if (task.getTaskRegion().notAvailable()) {
            Notifier.error("在任务区域未初始化时执行了任务的preload方法");
            return;
        }
        IOC.getBean(WorldEditAPI.class).pasteWorkingUnit(new LocationCommandSender(task.getTaskRegion().getTaskArea().getCenter().clone().add(0, 2, 0)));
        List<Location> portalLocations = IOC.getBean(BlockAPI.class)
                .searchBlock(Material.END_PORTAL_FRAME, task.getTaskRegion().getTaskArea().getCenter(), 30);
        task.getTaskRegion().getSpawnZone().setSpawnLocations(portalLocations);
        // 战利品箱子数量
        int lootChestAmount = 0;
        // 生成初始战利品
        List<Location> chestLocations = IOC.getBean(BlockAPI.class).searchBlock(Material.CHEST, task.getTaskRegion().getTaskArea().getCenter(), 30);
        for (Location location : chestLocations) {
            if (location.getBlock().getType() != Material.CHEST) continue;
            if (lootChestAmount >= task.getTaskTeamSize()) {
                task.getSubScheduler().runTask(() -> location.getBlock().setType(Material.AIR));
                continue;
            } // 跟人数有关
            task.getSubScheduler().runTask(() -> {
                Chest chest = (Chest) location.getBlock().getState();
                new ChestLoot(chest).applyLootTable();
            });
            lootChestAmount++;
        }
        finishPreLoad = true;
    }

    @Override
    public boolean isPreloadFinished() {
        return finishPreLoad;
    }
}
