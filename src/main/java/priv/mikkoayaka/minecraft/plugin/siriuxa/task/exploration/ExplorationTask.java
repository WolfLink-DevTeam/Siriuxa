package priv.mikkoayaka.minecraft.plugin.siriuxa.task.exploration;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.LinearStageHolder;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.StageHolder;
import priv.mikkoayaka.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import priv.mikkoayaka.minecraft.plugin.siriuxa.file.Config;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.interfaces.HurtCheckAvailable;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.interfaces.OreCheckAvailable;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.stage.TaskLinearStageHolder;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.exploration.taskstage.EndStage;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.exploration.taskstage.GameStage;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.exploration.taskstage.ReadyStage;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.exploration.taskstage.WaitStage;
import priv.mikkoayaka.minecraft.plugin.siriuxa.team.TaskTeam;

/**
 * 自由探索任务
 * 活动区域大
 */
public class ExplorationTask extends Task implements HurtCheckAvailable, OreCheckAvailable {
    @Getter
    private final ExplorationDifficulty difficulty;
    @Getter
    private final LinearStageHolder stageHolder = (LinearStageHolder) super.getStageHolder();

    public ExplorationTask(TaskTeam taskTeam, ExplorationDifficulty difficulty) {
        super(taskTeam, difficulty);
        this.difficulty = difficulty;
    }

    @Override
    protected StageHolder initStageHolder() {
        TaskLinearStageHolder linearStageHolder = new TaskLinearStageHolder(this);
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
        for (Player player : getPlayers()) {
            player.teleport(lobbyLocation);
            player.sendTitle("§a任务完成", "§7前往领取本次任务的报酬吧", 10, 80, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
        }
        deleteTask();
    }

    @Override
    public void failed() {
        Config config = IOC.getBean(Config.class);
        Location lobbyLocation = config.getLobbyLocation();
        for (Player player : getPlayers()) {
            player.teleport(lobbyLocation);
            player.sendTitle("§c任务失败", "§7真可惜...下次再尝试吧", 10, 80, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.8f);
        }
        deleteTask();
    }

    @Override
    public boolean canJoin() {
        return getStageHolder().getThisStage() instanceof WaitStage;
    }

    @Override
    public double getHurtWheatCost() {
        return difficulty.getHurtWheatCost();
    }

    @Override
    public String getName() {
        return "自由勘探";
    }

    @Override
    public String getColor() {
        return "§f";
    }
}