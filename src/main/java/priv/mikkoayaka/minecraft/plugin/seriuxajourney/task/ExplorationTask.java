package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.LinearStageHolder;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Config;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.taskstage.EndStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.taskstage.GameStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.taskstage.ReadyStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.taskstage.WaitStage;

/**
 * 自由探索任务
 * 麦穗会流失
 * 活动区域大
 */
public class ExplorationTask extends Task {
    @Getter
    private final TaskDifficulty difficulty;
    @Getter
    private final LinearStageHolder linearStageHolder;

    public ExplorationTask(TaskDifficulty difficulty) {
        super(IOC.getBean(Config.class).getBaseWheatLoss());
        this.difficulty = difficulty;
        // 绑定阶段持有者和阶段实例
        linearStageHolder = new LinearStageHolder(false);
        linearStageHolder.bindStages(new Stage[]{
                new WaitStage(linearStageHolder),
                new ReadyStage(linearStageHolder),
                new GameStage(linearStageHolder),
                new EndStage(linearStageHolder)
        });
        // 进入等待阶段
        linearStageHolder.next();
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
                ()-> addWheatLossMultiple(difficulty.wheatLostAcceleratedSpeed())
                ,20 * 60 * 5,20 * 60 * 5).getTaskId();
    }
    private void stopTiming() {
        if(timingTask1Id != -1) Bukkit.getScheduler().cancelTask(timingTask1Id);
        if(timingTask2Id != -1) Bukkit.getScheduler().cancelTask(timingTask2Id);
    }

    @Override
    public void finish() {

    }

    @Override
    public void failed() {

    }
}
