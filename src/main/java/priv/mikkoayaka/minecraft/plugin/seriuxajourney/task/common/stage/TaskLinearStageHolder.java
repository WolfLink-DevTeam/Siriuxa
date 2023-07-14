package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.stage;

import lombok.Getter;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.LinearStageHolder;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;

public class TaskLinearStageHolder extends LinearStageHolder {
    @Getter
    private final Task task;
    public TaskLinearStageHolder(Task task, boolean recycle) {
        super(recycle);
        this.task = task;
    }
}
