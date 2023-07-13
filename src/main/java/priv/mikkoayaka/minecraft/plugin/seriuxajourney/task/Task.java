package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task;

import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.region.TaskRegion;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 抽象任务类
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
     * 本次任务的麦穗余量
     */
    private double taskWheat = 0;
    /**
     * 本次任务玩家的 UUID 集合
     */
    private final Set<UUID> playerUuids = new HashSet<>();
    @Nullable
    private TaskRegion taskRegion = null;

    /**
     * 当前可用的撤离点
     */
    private EvacuationZone availableEvacuationZone = null;

    public Task(double baseWheatLoss) {
        synchronized (this) {
            taskId = maxTaskId;
            maxTaskId++;
        }
        this.baseWheatLoss = baseWheatLoss;
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
    public Set<Player> getPlayers() {
        return playerUuids.stream()
                .map(Bukkit::getPlayer)
                .filter(p -> p!=null&&p.isOnline())
                .collect(Collectors.toSet());
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
                return;
            }
            if(waitForEvacuatePlayers().size() == playerUuids.size()) {
                stopCheck();
                finish();
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
        taskRegion.startCheck();
        getPlayers().forEach(p -> p.teleport(taskRegion.getCenter()));
        startGameOverCheck();
        evacuateTaskId = Bukkit.getScheduler().runTaskTimer(SeriuxaJourney.getInstance(),()->{
            Location evacuateLocation = taskRegion.getEvacuateLocation((int) taskRegion.getRadius());
            if(evacuateLocation == null) {
                stopCheck();
                failed();
                return;
            }
            availableEvacuationZone = new EvacuationZone(evacuateLocation,5);
        },20 * 60 * 30,20 * 60 * 15).getTaskId();
    }

    private void stopCheck() {
        if(taskRegion != null) taskRegion.stopCheck();
        stopEvacuateTask();
        stopFinishCheck();
    }
    /**
     * 任务玩家全部撤离时任务完成
     */
    public abstract void finish();

    /**
     * 麦穗为0，或玩家全部逃跑时，任务失败
     */
    public abstract void failed();
}
