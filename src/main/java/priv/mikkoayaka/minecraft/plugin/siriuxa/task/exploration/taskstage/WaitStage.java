package priv.mikkoayaka.minecraft.plugin.siriuxa.task.exploration.taskstage;

import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.stage.TaskLinearStageHolder;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.stage.TaskStage;

public class WaitStage extends TaskStage {
    public WaitStage(TaskLinearStageHolder stageHolder) {
        super("等待中", stageHolder);
    }
}
