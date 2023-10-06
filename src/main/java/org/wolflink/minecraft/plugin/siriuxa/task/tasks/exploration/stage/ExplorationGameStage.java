package org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration.stage;

import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;

public class ExplorationGameStage extends GameStage {
    private final Config config;
    private final Task task;

    public ExplorationGameStage(TaskLinearStageHolder stageHolder) {
        super(stageHolder);
        task = stageHolder.getTask();
        config = IOC.getBean(Config.class);
    }

    @Override
    protected void onEnter() {
        super.onEnter();
        getSubScheduler().runTaskAsync(() -> {
            // 阻塞直到任务加载完成
            while (!task.isFinishPreLoad()) {
                task.getGlobalTeam().getPlayers().forEach(player -> player.sendTitle(" ", "§e任务区域仍在加载中，已超出预计时间，请耐心等待...", 4, 12, 4));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            getSubScheduler().runTask(() -> getStageHolder().getTask().start());
        });

    }

    @Override
    protected void onLeave() {
        super.onLeave();
    }
}
