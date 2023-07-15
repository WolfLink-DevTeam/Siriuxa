package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common;

import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.StageHolder;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.BlockAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.WorldEditAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.region.TaskRegion;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 抽象任务类
 * 麦穗会流失
 */
@Data
public abstract class Task {

    private static int maxTaskId = 1;
    /**
     * 任务ID
     */
    private final int taskId;
    /**
     * 基础麦穗流失量(每秒)
     */
    private final double baseWheatLoss;
    /**
     * 麦穗流失倍率
     */
    private double wheatLossMultiple = 1.0;
    /**
     * 麦穗流失加速度
     */
    private double wheatLostAcceleratedSpeed;
    /**
     * 本次任务的麦穗余量
     */
    private double taskWheat = 0;
    /**
     * 本次任务玩家的 UUID 集合
     */
    private final Set<UUID> playerUuids = new HashSet<>();
    @Nullable
    private TaskRegion taskRegion = null;

    private TaskDifficulty difficulty;

    /**
     * 当前可用的撤离点
     */
    private EvacuationZone availableEvacuationZone = null;

    @Getter
    private final StageHolder stageHolder;
    protected abstract StageHolder initStageHolder();

    public Task(TaskDifficulty taskDifficulty) {
        synchronized (this) {
            taskId = maxTaskId;
            maxTaskId++;
        }
        this.difficulty = taskDifficulty;
        this.wheatLostAcceleratedSpeed = taskDifficulty.getWheatLostAcceleratedSpeed();
        this.baseWheatLoss = taskDifficulty.getBaseWheatLoss();
        stageHolder = initStageHolder();
    }
    public void addWheat(double wheat) {
        taskWheat += wheat;
    }
    public void takeWheat(double wheat) {
        taskWheat -= wheat;
        if(taskWheat <= 0) {
            taskWheat = 0;
            failed();
        }
    }
    public void takeWheat(double wheat,String reason) {
        takeWheat(wheat);
        Notifier.broadcastChat(playerUuids,"本次任务损失了 "+wheat+" 麦穗，原因是"+reason);
    }
    public List<Player> getPlayers() {
        return playerUuids.stream()
                .map(Bukkit::getPlayer)
                .filter(p -> p!=null&&p.isOnline())
                .collect(Collectors.toList());
    }
    public void addWheatLossMultiple(double value) {
        wheatLossMultiple += value;
    }
    public Set<Player> waitForEvacuatePlayers() {
        if(availableEvacuationZone == null) return new HashSet<>();
        else return availableEvacuationZone.getPlayerInZone();
    }

    private int finishCheckTaskId = -1;

    /**
     * 游戏结束检查
     * 如果本次任务玩家数为0则意味着所有玩家逃跑/离线，宣布任务失败
     * 如果撤离玩家数和任务玩家数一致，则任务完成
     */
    public void startGameOverCheck() {
        finishCheckTaskId = Bukkit.getScheduler().runTaskTimer(SeriuxaJourney.getInstance(),()->{
            if(playerUuids.size() == 0) {
                stopCheck();
                failed();
                stageHolder.next();
                return;
            }
            if(waitForEvacuatePlayers().size() == playerUuids.size()) {
                stopCheck();
                finish();
                stageHolder.next();
                return;
            }
        },20,20).getTaskId();
    }
    public void stopFinishCheck() {
        if(finishCheckTaskId != -1) {
            Bukkit.getScheduler().cancelTask(finishCheckTaskId);
            finishCheckTaskId = -1;
        }
    }

    private int evacuateTaskId = -1;

    /**
     * 停止生成撤离点
     */
    public void stopEvacuateTask() {
        if(evacuateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(evacuateTaskId);
            evacuateTaskId = -1;
        }
    }
    public void start(TaskRegion taskRegion) {
        this.taskRegion = taskRegion;
        Bukkit.getScheduler().runTaskAsynchronously(SeriuxaJourney.getInstance(),()->{
            IOC.getBean(WorldEditAPI.class).pasteWorkingUnit(taskRegion.getCenter());
            List<Location> beaconLocations = IOC.getBean(BlockAPI.class).searchBlock(Material.BEACON,taskRegion.getCenter(),20);
            Bukkit.getScheduler().runTask(SeriuxaJourney.getInstance(),()->{
                List<Player> playerList = getPlayers();
                for (int i = 0; i < playerList.size(); i++) {
                    Player player = playerList.get(i);
                    player.teleport(beaconLocations.get(i%beaconLocations.size()));
                }
                startGameOverCheck();
                startTiming();
                startEvacuateTask();
                taskRegion.startCheck();
            });
        });
    }
    private void startEvacuateTask() {
        evacuateTaskId = Bukkit.getScheduler().runTaskTimer(SeriuxaJourney.getInstance(),()->{
            if(taskRegion == null) return;
            Location evacuateLocation = taskRegion.getEvacuateLocation((int) taskRegion.getRadius());
            if(evacuateLocation == null) {
                stopCheck();
                failed();
                return;
            }
            if(availableEvacuationZone != null) {
                Notifier.broadcastChat(getPlayers(),"坐标 X："+availableEvacuationZone.getCenter().getBlockX()+"Z："+availableEvacuationZone.getCenter().getBlockZ()+" 附近的飞艇已撤离，请等待下一艘飞艇接应。");
                availableEvacuationZone = null;
            } else {
                availableEvacuationZone = new EvacuationZone(evacuateLocation,5);
                Notifier.broadcastChat(getPlayers(),"飞艇已降落至坐标 X："+evacuateLocation.getBlockX()+" Z："+evacuateLocation.getBlockZ()+" 如有需要请尽快前往撤离。");
            }
            //TODO 30|15
        },20 * 60 * 1,20 * 60 * 1).getTaskId();
    }

    /**
     * 获取当前麦穗每秒流失量
     */
    public double getWheatLossPerSecNow() {
        return baseWheatLoss * wheatLossMultiple;
    }
    int timingTask1Id = -1;
    int timingTask2Id = -1;
    private void startTiming() {
        timingTask1Id =
                Bukkit.getScheduler().runTaskTimer(SeriuxaJourney.getInstance(),
                        ()-> takeWheat(getBaseWheatLoss() * getWheatLossMultiple())
                        ,20,20).getTaskId();
        timingTask2Id =
                Bukkit.getScheduler().runTaskTimer(SeriuxaJourney.getInstance(),
                        ()-> addWheatLossMultiple(wheatLostAcceleratedSpeed)
                        ,20 * 60 * 5,20 * 60 * 5).getTaskId();
    }
    private void stopTiming() {
        if(timingTask1Id != -1) Bukkit.getScheduler().cancelTask(timingTask1Id);
        if(timingTask2Id != -1) Bukkit.getScheduler().cancelTask(timingTask2Id);
    }

    private void stopCheck() {
        stopTiming();
        if(taskRegion != null) taskRegion.stopCheck();
        stopEvacuateTask();
        stopFinishCheck();
    }
    /**
     * 任务玩家全部撤离时任务完成
     */
    protected abstract void finish();

    /**
     * 麦穗为0，或玩家全部逃跑时，任务失败
     */
    protected abstract void failed();

    /**
     * 是否允许其他玩家加入
     */
    public abstract boolean canJoin();

    /**
     * 清理本次任务
     * 在任务完成/失败后调用
     */
    protected void clearTask() {
        playerUuids.clear();
        IOC.getBean(TaskRepository.class).deleteByKey(taskId);
    }
}
