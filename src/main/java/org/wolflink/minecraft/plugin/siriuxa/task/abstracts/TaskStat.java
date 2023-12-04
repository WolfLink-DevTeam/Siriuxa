package org.wolflink.minecraft.plugin.siriuxa.task.abstracts;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.IStatus;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.listeners.StatListener;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用任务统计
 */
@Getter
public class TaskStat implements IStatus {

    protected boolean enabled = false;
    protected final Task task;
    protected final SubScheduler subScheduler = new SubScheduler();
    // 开始时间
    protected Calendar startTime = null;
    // 结束时间
    protected Calendar endTime = null;
    // 移动距离统计
    protected final Map<UUID, Integer> travelDistanceMap = new ConcurrentHashMap<>();
    // 造成伤害统计
    protected final Map<UUID, Integer> damageMap = new ConcurrentHashMap<>();
    // 怪物击杀统计
    protected final Map<UUID, Integer> mobKillMap = new ConcurrentHashMap<>();

    public TaskStat(Task task) {
        this.task = task;
    }

    public final long getUsingTimeInMills() {
        if (endTime == null || startTime == null) return -1;
        return endTime.getTimeInMillis() - startTime.getTimeInMillis();
    }

    @Override
    public void enable() {
        enabled = true;
        startTime = Calendar.getInstance();
        IOC.getBean(StatListener.class).addStat(this);
    }

    @Override
    public void disable() {
        enabled = false;
        IOC.getBean(StatListener.class).removeStat(this);
        endTime = Calendar.getInstance();
        subScheduler.cancelAllTasks();
    }

    /**
     * 计算玩家得分
     */
    public double getPlayerScore(UUID playerUuid) {
        int travelDistance = travelDistanceMap.getOrDefault(playerUuid, 0);
        int travelDistanceValue = (int) Math.pow(travelDistance, 0.4);
        int damageTotal = damageMap.getOrDefault(playerUuid, 0);
        int damageTotalValue = (int) Math.pow(damageTotal, 0.45);
        int mobKillTotal = mobKillMap.getOrDefault(playerUuid, 0);
        int mobKillTotalValue = (int) Math.pow(mobKillTotal, 0.65);
        int taskSecs = (int) ((Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) / 1000);
        int taskSecsValue = (int) (Math.pow(taskSecs, 0.82) / 15);
        int score = (travelDistanceValue + damageTotalValue + mobKillTotalValue + taskSecsValue);
        Notifier.debug("------------------------------");
        Notifier.debug("结算玩家：" + Bukkit.getOfflinePlayer(playerUuid).getName());
        Notifier.debug("行走距离：" + travelDistance + "米 => " + travelDistanceValue+"分");
        Notifier.debug("造成伤害：" + damageTotal + "点 => " + damageTotalValue+"分");
        Notifier.debug("怪物击杀：" + mobKillTotal + "个 => " + mobKillTotalValue+"分");
        Notifier.debug("任务用时：" + taskSecs + "秒 => " + taskSecsValue+"分");
        Notifier.debug("总计得分：" + score+"分");
        Notifier.debug("------------------------------");
        return score;
    }
}
