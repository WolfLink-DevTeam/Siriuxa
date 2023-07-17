package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common;

import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.Result;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.VaultAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.ExplorationDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Config;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationTask;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.GameStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.WaitStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.team.TaskTeam;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.team.TaskTeamRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.team.TaskTeamService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

@Singleton
public class TaskService {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private TaskTeamRepository taskTeamRepository;
    @Inject
    private TaskTeamService taskTeamService;
    @Inject
    private VaultAPI vaultAPI;
    @Inject
    private Config config;

    public TaskService() {
    }

    public Result create(Player player, Class<? extends Task> taskClass, TaskDifficulty taskDifficulty) {
        TaskTeam taskTeam = taskTeamRepository.findByPlayer(player);
        if (taskTeam == null) {
            Result result = taskTeamService.createTeam(player);
            if (!result.result()) return result; // 队伍创建失败
            taskTeam = taskTeamRepository.findByPlayer(player);
        }
        if (taskTeam == null) return new Result(false, "玩家创建了队伍但未找到所在队伍");
        return create(taskTeam, taskClass, taskDifficulty);
    }

    public Result create(TaskTeam taskTeam, Class<? extends Task> taskClass, TaskDifficulty taskDifficulty) {

        if (taskClass.equals(ExplorationTask.class)) {
            return IOC.getBean(ExplorationService.class).create(taskTeam, (ExplorationDifficulty) taskDifficulty);
        } else {
            return new Result(false, "暂不支持的任务类型");
        }

    }

    public Result ready(Task task) {
        if (task == null) return new Result(false, "不存在的任务。");
        if (task.getPlayers().size() == 0) {
            taskRepository.deleteByKey(task.getTaskUuid());
            return new Result(false, "该任务没有任何在线玩家。");
        }
        if (task.getStageHolder().getThisStage() instanceof WaitStage) {
            task.getStageHolder().next();
            return new Result(true, "任务即将开始。");
        }
        return new Result(false, "任务当前不处于等待阶段，无法准备。");
    }

    /**
     * 删除任务，通知并解绑相关队伍
     * <p>
     * 任务如果在进行准备，进行阶段，结束阶段，会将所有玩家传送到大厅
     */
    public Result delete(Task task) {
        TaskTeam taskTeam = task.getTaskTeam();
        if (taskTeam != null) {
            Notifier.broadcastChat(taskTeam.getPlayers(), "§e队伍所选任务已被删除，请重新选择。");
            Stage stage = task.getStageHolder().getThisStage();
            if (!(stage instanceof WaitStage)) {
                for (Player player : taskTeam.getPlayers()) {
                    player.teleport(config.getLobbyLocation());
                }
            }
            taskTeam.setSelectedTask(null);
            task.setTaskTeam(new TaskTeam());
        }
        if (task.getStageHolder().getThisStage() instanceof GameStage) {
            task.failed();
        }
        taskRepository.deleteByKey(task.getTaskUuid());
        return new Result(true, "任务删除成功。");
    }
}
