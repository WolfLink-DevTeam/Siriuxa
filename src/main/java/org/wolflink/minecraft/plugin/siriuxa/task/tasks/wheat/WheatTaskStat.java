package org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat;

import org.bukkit.Bukkit;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskStat;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

/**
 * 麦穗任务统计
 */
public class WheatTaskStat extends TaskStat {
    private double lastWheat = 0;
    private double nowWheat = 0;

    private final WheatTask wheatTask;
    public WheatTaskStat(WheatTask wheatTask) {
        super(wheatTask);
        this.wheatTask = wheatTask;
    }

    public double getWheatChange() {
        return nowWheat - lastWheat;
    }

    @Override
    public void enable() {
        super.enable();
        subScheduler.runTaskTimerAsync(() -> {
            lastWheat = nowWheat;
            nowWheat = wheatTask.getTaskWheat();
        }, 20, 20);
    }
    /**
     * 根据统计数据计算玩家麦穗奖励
     */
    public double getPlayerWheatReward(UUID uuid) {
        int travelDistance = travelDistanceMap.getOrDefault(uuid,0);
        int travelDistanceValue = (int) Math.pow(travelDistance,0.4);
        int damageTotal = damageMap.getOrDefault(uuid,0);
        int damageTotalValue = (int) Math.pow(damageTotal,0.45);
        int oreBlockTotal = oreSellMap.getOrDefault(uuid,0);
        int oreBlockTotalValue = (int) Math.pow(oreBlockTotal,0.8);
        int mobKillTotal = mobKillMap.getOrDefault(uuid,0);
        int mobKillTotalValue = (int) Math.pow(mobKillTotal,0.65);
        int taskSecs = (int) ((Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())/1000);
        int taskSecsValue = (int) (15 + Math.pow(taskSecs,0.85)/20);
        int randValue = new Random().nextInt(10,50);
        int reward = (int) ((travelDistanceValue + damageTotalValue + oreBlockTotalValue + mobKillTotalValue + taskSecsValue + randValue) * wheatTask.getDifficulty().getRewardMultiple());
        Notifier.debug("结算玩家："+ Bukkit.getOfflinePlayer(uuid).getName());
        Notifier.debug("行走距离："+travelDistance+"|"+travelDistanceValue);
        Notifier.debug("造成伤害："+damageTotal+"|"+damageTotalValue);
        Notifier.debug("出售矿物："+oreBlockTotal+"|"+oreBlockTotalValue);
        Notifier.debug("怪物击杀："+mobKillTotal+"|"+mobKillTotalValue);
        Notifier.debug("任务用时："+taskSecs+"|"+taskSecsValue);
        Notifier.debug("随机奖励："+randValue);
        Notifier.debug("奖励倍率："+wheatTask.getDifficulty().getRewardMultiple());
        Notifier.debug("合计："+reward);
        return reward;
    }
}
