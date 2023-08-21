package org.wolflink.minecraft.plugin.siriuxa.task.tasks.lumen;

import org.bukkit.Bukkit;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskStat;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

/**
 * 麦穗任务统计
 */
public class LumenTaskStat extends TaskStat {
    private double lastWheat = 0;
    private double nowWheat = 0;

    private final LumenTask lumenTask;
    public LumenTaskStat(LumenTask lumenTask) {
        super(lumenTask);
        this.lumenTask = lumenTask;
    }

    public double getWheatChange() {
        return nowWheat - lastWheat;
    }

    @Override
    public void enable() {
        super.enable();
        subScheduler.runTaskTimerAsync(() -> {
            lastWheat = nowWheat;
            nowWheat = lumenTask.getTaskWheat();
        }, 20, 20);
    }
    /**
     * 根据统计数据计算玩家麦穗奖励
     * 如果不带物品进入，额外奖励50%麦穗
     */
    public double getPlayerWheatReward(final UUID uuid,final double rewardMultiple,final boolean emptyBackpack) {

        double multiple = rewardMultiple;
        if(emptyBackpack) multiple += 0.5;
        int travelDistance = travelDistanceMap.getOrDefault(uuid,0);
        int travelDistanceValue = (int) Math.pow(travelDistance,0.4);
        int damageTotal = damageMap.getOrDefault(uuid,0);
        int damageTotalValue = (int) Math.pow(damageTotal,0.45);
        int oreBlockTotal = oreSellMap.getOrDefault(uuid,0);
        int oreBlockTotalValue = (int) Math.pow(oreBlockTotal,0.8);
        int mobKillTotal = mobKillMap.getOrDefault(uuid,0);
        int mobKillTotalValue = (int) Math.pow(mobKillTotal,0.65);
        int taskSecs = (int) ((Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())/1000);
        int taskSecsValue = (int) (Math.pow(taskSecs,0.82)/15);
        int randValue = new Random().nextInt(10,20);
        int originReward = (travelDistanceValue + damageTotalValue + oreBlockTotalValue + mobKillTotalValue + taskSecsValue + randValue);
        if(originReward >= 300) originReward = 300;
        int reward = (int) (originReward * lumenTask.getDifficulty().getRewardMultiple());
        Notifier.debug("结算玩家："+ Bukkit.getOfflinePlayer(uuid).getName());
        Notifier.debug("行走距离："+travelDistance+"|"+travelDistanceValue);
        Notifier.debug("造成伤害："+damageTotal+"|"+damageTotalValue);
        Notifier.debug("出售矿物："+oreBlockTotal+"|"+oreBlockTotalValue);
        Notifier.debug("怪物击杀："+mobKillTotal+"|"+mobKillTotalValue);
        Notifier.debug("任务用时："+taskSecs+"|"+taskSecsValue);
        Notifier.debug("随机奖励："+randValue);
        Notifier.debug("奖励倍率："+multiple);
        Notifier.debug("不携带装备入场："+emptyBackpack);
        Notifier.debug("合计："+reward);
        return reward;
    }
}
