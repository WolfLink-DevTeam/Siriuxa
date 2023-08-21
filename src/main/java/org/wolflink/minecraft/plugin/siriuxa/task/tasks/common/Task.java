package org.wolflink.minecraft.plugin.siriuxa.task.tasks.common;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.INameable;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationCommandSender;
import org.wolflink.minecraft.plugin.siriuxa.api.world.RegionAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.world.WorldEditAPI;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.plugin.siriuxa.backpack.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerTaskRecord;
import org.wolflink.minecraft.plugin.siriuxa.monster.StrategyDecider;
import org.wolflink.minecraft.plugin.siriuxa.task.interfaces.IGlobalTeam;
import org.wolflink.minecraft.plugin.siriuxa.task.interfaces.IRecordable;
import org.wolflink.minecraft.plugin.siriuxa.task.interfaces.ITaskTeam;
import org.wolflink.minecraft.plugin.siriuxa.task.ornaments.OrnamentType;
import org.wolflink.minecraft.plugin.siriuxa.task.regions.SquareArea;
import org.wolflink.minecraft.plugin.siriuxa.task.regions.TaskArea;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeam;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.StageHolder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象任务类
 */
@Data
public abstract class Task implements IGlobalTeam, ITaskTeam,IRecordable,INameable {

    public abstract Set<OrnamentType> getOrnamentTypes();
    protected final SubScheduler subScheduler = new SubScheduler();
    protected final UUID taskUuid = UUID.randomUUID();
    private final TaskDifficulty taskDifficulty;
    protected final Random random = new Random();
    private final StageHolder stageHolder;
    private final PlayerBackpack defaultKit;
    private final StrategyDecider strategyDecider;

    @Nullable
    private TaskArea taskArea = null;
    @NonNull
    GlobalTeam globalTeam;
    @Nullable
    TaskTeam taskTeam = null;
    protected Task(@NotNull GlobalTeam globalTeam,
                   @NotNull TaskDifficulty taskDifficulty,
                   @NotNull PlayerBackpack defaultKit) {
        this.globalTeam = globalTeam;
        this.taskDifficulty = taskDifficulty;
        this.defaultKit = defaultKit;
        stageHolder = initStageHolder();
        strategyDecider = new StrategyDecider(this);
    }
    protected abstract StageHolder initStageHolder();
    protected void triggerFailed() {
        getTaskPlayers().forEach(player -> fillRecord(player, false));
        stageHolder.next();
        getTaskStat().disable();
        stopCheck();
        finishRecord();
        failed();
        for (Player player : getGlobalTeam().getPlayers()) {
            IOC.getBean(TaskService.class).goLobby(player);
            Siriuxa.getInstance().getSubScheduler().runTaskLater(() -> {
                player.sendTitle("§c任务失败", "§7嘿！别灰心丧气的，下次加油！", 10, 80, 10);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.8f);
            }, 20 * 3L);
        }
        deleteTask();
    }

    private final Map<UUID, PlayerTaskRecord> playerRecordMap = new ConcurrentHashMap<>();
    @Override
    public Map<UUID, PlayerTaskRecord> getPlayerRecordMap() {
        return playerRecordMap;
    }

    protected void triggerFinish() {
        triggerFinish(false);
    }
    protected void triggerFinish(boolean isServerClosing) {
        getTaskPlayers().forEach(player -> fillRecord(player, true));
        stageHolder.next();
        getTaskStat().disable();
        stopCheck();
        finishRecord();
        finish();
        for (Player player : getGlobalTeam().getPlayers()) {
            IOC.getBean(TaskService.class).goLobby(player);
            if(!isServerClosing) {
                Siriuxa.getInstance().getSubScheduler().runTaskLater(() -> {
                    player.sendTitle("§a任务完成", "§7前往领取本次任务的报酬吧", 10, 80, 10);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
                    player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
                }, 20 * 3L);
            }
        }
        deleteTask();
    }
    protected abstract void failedCheck();
    protected abstract void finishedCheck();

    public void preLoad() {
        this.taskTeam = new TaskTeam(getGlobalTeam());
        String worldName = IOC.getBean(Config.class).get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME);
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Notifier.error(worldName + "世界不存在！请检查配置文件");
            return;
        }
        Location regionCenter = IOC.getBean(RegionAPI.class).autoGetRegionCenter(world);
        this.taskArea = new SquareArea(this, regionCenter);
        IOC.getBean(WorldEditAPI.class).pasteWorkingUnit(new LocationCommandSender(taskArea.getCenter().clone().add(0, 2, 0)));
        implPreLoad();
    }
    @Getter
    protected boolean finishPreLoad = false;
    protected abstract void implPreLoad();

    public abstract void start();

    private void stopCheck() {
        strategyDecider.disable();
        subScheduler.cancelAllTasks();
        if (taskArea != null) {
            taskArea.stopCheck();
            taskArea = null;
        }
    }

    /**
     * 任务玩家全部撤离时任务完成
     */
    protected abstract void finish();

    /**
     * 麦穗为0，或玩家全部逃跑时，任务失败
     */
    public abstract void failed();

    /**
     * 清理本次任务
     * 在任务完成/失败后调用
     */
    protected void deleteTask() {
        globalTeam.setSelectedTask(null);
        if(taskTeam != null) taskTeam.clear();
        IOC.getBean(TaskRepository.class).deleteByKey(taskUuid);
        // 释放队列
        IOC.getBean(TaskQueue.class).taskEnded();
        globalTeam = new GlobalTeam(null);
    }


    public void death(Player player) {
        if(taskTeam == null) {
            Notifier.error("任务的队伍未被初始化！");
            return;
        }
        fillRecord(player, false);
        taskTeam.leave(player);
        player.setGameMode(GameMode.SPECTATOR);
        Notifier.debug("玩家" + player.getName() + "在任务中阵亡了。");
        Notifier.broadcastChat(taskTeam.getPlayers(), "玩家" + player.getName() + "在任务中阵亡了。");
        player.sendTitle("§c§l寄！", "§7胜败乃兵家常事，大侠请重新来过。", 10, 80, 10);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 0.5f);
        if (taskTeam.isEmpty()) triggerFailed();
    }

    /**
     * 玩家逃跑
     * (适用于任务过程中玩家非正常离开任务的情况)
     */
    public void escape(OfflinePlayer offlinePlayer) {
        if(taskTeam == null) {
            Notifier.error("任务的队伍未被初始化！");
            return;
        }
        fillRecord(offlinePlayer, false);
        taskTeam.leave(offlinePlayer);
        Notifier.debug("玩家" + offlinePlayer.getName() + "在任务过程中失踪了。");
        Notifier.broadcastChat(taskTeam.getPlayers(), "玩家" + offlinePlayer.getName() + "在任务过程中失踪了。");
    }
    @Getter
    @Setter
    private List<Location> spawnLocations = new ArrayList<>();

}
