package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Config;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.ConfigProjection;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.region.SquareRegion;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.stage.TaskLinearStageHolder;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.stage.TaskStage;

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
        String worldName = config.get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME);
        World world = Bukkit.getWorld(worldName);
        getStageHolder().getTask().start(new SquareRegion(
                getStageHolder().getTask(),
                world,
                (int) ((2 * Math.random() * 10000000) - 10000000),
                (int) ((2 * Math.random() * 10000000) - 10000000),
                500
        ));
    }
    @Override
    protected void onLeave() {

    }
}
