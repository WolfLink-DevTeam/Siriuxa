package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task;

import lombok.Getter;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.LinearStageHolder;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.taskstage.EndStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.taskstage.GameStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.taskstage.ReadyStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.taskstage.WaitStage;

import java.util.Set;
import java.util.UUID;

/**
 * 自由探索任务
 */
public class ExplorationTask extends Task {
    @Getter
    private final TaskDifficulty difficulty;
    @Getter
    private final LinearStageHolder linearStageHolder;

    public ExplorationTask(TaskDifficulty difficulty) {
        super(0.1);
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
}
