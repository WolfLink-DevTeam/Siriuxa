package priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.stage;

import lombok.Getter;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.LinearStageHolder;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.Task;

public class TaskLinearStageHolder extends LinearStageHolder {
    @Getter
    private final Task task;

    public TaskLinearStageHolder(Task task) {
        super(false);
        this.task = task;
    }
}
