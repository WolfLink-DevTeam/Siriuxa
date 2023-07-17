package priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.stage;

import lombok.Getter;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import priv.mikkoayaka.minecraft.plugin.siriuxa.utils.Notifier;

public abstract class TaskStage extends Stage {

    @Getter
    private final TaskLinearStageHolder stageHolder;

    public TaskStage(String displayName, TaskLinearStageHolder stageHolder) {
        super(displayName, stageHolder);
        this.stageHolder = stageHolder;
    }

    @Override
    protected void onEnter() {
        Notifier.debug("任务" + getStageHolder().getTask().getTaskUuid().toString() + "进入" + getDisplayName() + "阶段");
    }

    @Override
    protected void onLeave() {
        Notifier.debug("任务" + getStageHolder().getTask().getTaskUuid().toString() + "离开" + getDisplayName() + "阶段");
    }
}
