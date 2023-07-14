package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.LinearStageHolder;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.StageHolder;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Config;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.EndStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.GameStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.ReadyStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.WaitStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

/**
 * 自由探索任务
 * 活动区域大
 */
public class ExplorationTask extends Task {
    @Getter
    private final TaskDifficulty difficulty;

    public ExplorationTask(TaskDifficulty difficulty) {
        super(IOC.getBean(Config.class).getBaseWheatLoss(),difficulty.wheatLostAcceleratedSpeed());
        this.difficulty = difficulty;
    }

    @Override
    protected StageHolder initStageHolder() {
        LinearStageHolder linearStageHolder = new LinearStageHolder(false);
        linearStageHolder.bindStages(new Stage[]{
                new WaitStage(linearStageHolder),
                new ReadyStage(linearStageHolder),
                new GameStage(linearStageHolder),
                new EndStage(linearStageHolder)
        });
        // 进入等待阶段
        linearStageHolder.next();
        return linearStageHolder;
    }

    @Override
    public void finish() {
        Config config = IOC.getBean(Config.class);
        Location lobbyLocation = config.getLobbyLocation();
        if(lobbyLocation == null) {
            Notifier.error("大厅坐标未设置！");
            return;
        }
        for (Player player : getPlayers()) {
            player.teleport(lobbyLocation);
            player.sendTitle("§a任务完成","§7前往领取本次任务的报酬吧",10,80,10);
            player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1.2f);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST,1f,1f);
        }
        clearTask();
    }

    @Override
    public void failed() {
        Config config = IOC.getBean(Config.class);
        Location lobbyLocation = config.getLobbyLocation();
        if(lobbyLocation == null) {
            Notifier.error("大厅坐标未设置！");
            return;
        }
        for (Player player : getPlayers()) {
            player.teleport(lobbyLocation);
            player.sendTitle("§c任务失败","§7真可惜...下次再尝试吧",10,80,10);
            player.playSound(player.getLocation(),Sound.ENTITY_VILLAGER_NO,1f,0.8f);
        }
        clearTask();
    }

    @Override
    public boolean canJoin() {
        return getStageHolder().getThisStage() instanceof WaitStage;
    }
}
