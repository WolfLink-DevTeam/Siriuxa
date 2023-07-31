package org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat;

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
        int damageTotal = damageMap.getOrDefault(uuid,0);
        int oreBlockTotal = oreSellMap.getOrDefault(uuid,0);
        int mobKillTotal = mobKillMap.getOrDefault(uuid,0);
        int taskSecs = (int) ((Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())/1000);
        return (Math.pow(travelDistance,0.4)
                        +Math.pow(damageTotal,0.45)
                        +Math.pow(oreBlockTotal,0.8)
                        +Math.pow(mobKillTotal,0.65)
                        +(15 + Math.pow(taskSecs,0.85)/20)
                        +new Random().nextInt(50,100)) * wheatTask.getDifficulty().getRewardMultiple();
    }
}
