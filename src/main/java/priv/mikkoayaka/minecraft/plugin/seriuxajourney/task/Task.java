package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
     * 本次任务的麦穗余量
     */
    private double taskWheat = 0;
    /**
     * 本次任务玩家的 UUID 集合
     */
    private final Set<UUID> playerUuids = new HashSet<>();

    public Task(double baseWheatLoss) {
        synchronized (this) {
            taskId = maxTaskId;
            maxTaskId++;
        }
        this.baseWheatLoss = baseWheatLoss;
    }
    public void takeWheat(double wheat) {
        taskWheat -= wheat;
    }
    public void takeWheat(double wheat,String reason) {
        takeWheat(wheat);
        Notifier.broadcastChat(playerUuids,"本次任务损失了 "+wheat+" 麦穗，原因是"+reason);
    }
}
