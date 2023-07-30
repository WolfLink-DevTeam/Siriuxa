package org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.taskstage;

import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskStage;

public class GameStage extends TaskStage {
    private final Config config;
    private final Task task;

    public GameStage(TaskLinearStageHolder stageHolder) {
        super("正在进行", stageHolder);
        task = stageHolder.getTask();
        config = IOC.getBean(Config.class);
    }

    @Override
    protected void onEnter() {
        super.onEnter();
        getStageHolder().getTask().start();
    }

    @Override
    protected void onLeave() {
        super.onLeave();
    }
}
