package priv.mikkoayaka.minecraft.plugin.seriuxajourney.team;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.Result;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.VaultAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Config;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.TaskRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationTask;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.GameStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.ReadyStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.WaitStage;

@Singleton
public class TaskTeamService {

    @Inject
    private TaskTeamRepository taskTeamRepository;
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private VaultAPI vaultAPI;
    @Inject
    private Config config;
    public Result createTeam(Player player) {
        if(taskTeamRepository.findByPlayer(player) != null) return new Result(false,"你当前已处于其它队伍中，无法创建队伍。");
        TaskTeam taskTeam = new TaskTeam();
        taskTeam.join(player);
        taskTeamRepository.insert(taskTeam);
        return new Result(true,"队伍创建成功。");
    }
    public TaskTeam createTeam() {
        TaskTeam taskTeam = new TaskTeam();
        taskTeamRepository.insert(taskTeam);
        return taskTeam;
    }

    /**
     * 解散队伍
     * 队伍的任务也会删除
     */
    public Result deleteTeam(TaskTeam taskTeam) {
        taskTeam.clear();
        Task task = taskTeam.getSelectedTask();
        if(task != null) {
            // TODO 暂时只有这种类的任务，强转了
            ExplorationService explorationService = IOC.getBean(ExplorationService.class);
            explorationService.deleteTask((ExplorationTask) task);
        }
        taskTeamRepository.deleteByKey(taskTeam.getTeamUuid());
        return new Result(true,"队伍已解散。");
    }

    /**
     * 加入队伍
     */
    public Result joinTeam(Player player,TaskTeam taskTeam) {
        if(taskTeamRepository.findByPlayer(player) != null) {
            return new Result(false,"玩家当前已处在其他队伍中，不允许加入。");
        }
        Task task = taskTeam.getSelectedTask();
        if(task != null) {
            Stage stage = task.getStageHolder().getThisStage();
            if(!(stage instanceof WaitStage)) {
                return new Result(false,"当前任务状态为："+stage.getDisplayName()+"，不允许加入。");
            }
            int wheatCost = task.getTaskDifficulty().getWheatCost();
            if(vaultAPI.getEconomy().getBalance(player) < wheatCost) {
                return new Result(false,"你需要支付 "+wheatCost+" 才能加入这次任务，显然你还没有足够的麦穗。");
            }
            vaultAPI.takeEconomy(player,wheatCost);
        }
        taskTeam.join(player);
        return new Result(true,"加入成功");
    }

    /**
     * 离开队伍，同时也会离开当前任务
     */
    public Result leaveTeam(Player player) {
        TaskTeam taskTeam = taskTeamRepository.findByPlayer(player);
        if(taskTeam == null) return new Result(false,"你没有在队伍中。");
        Task task = taskTeam.getSelectedTask();
        taskTeam.leave(player);
        if(task != null) {
            if(taskTeam.size() == 0) {
                taskTeamRepository.deleteByKey(taskTeam.getTeamUuid());
                if(task.getStageHolder().getThisStage() instanceof GameStage) {
                    task.failed();
                }
                taskRepository.deleteByKey(task.getTaskUuid());
            }
            // TODO 玩家背包，等级
            player.teleport(config.getLobbyLocation());

            Stage stage = task.getStageHolder().getThisStage();
            int wheatCost = task.getTaskDifficulty().getWheatCost();
            int wheatSupply = task.getTaskDifficulty().getWheatSupply();
            // 退回麦穗
            if(stage instanceof WaitStage || stage instanceof ReadyStage) {
                vaultAPI.addEconomy(player,wheatCost);
            } else {
                // 任务中扣除其一半麦穗
                task.takeWheat((wheatCost + wheatSupply) * 0.5,"玩家 "+player.getName()+" 中途退出了，但他仍保留下来一部分麦穗。");
            }
        }
        return new Result(true,"队伍退出成功。");
    }
}
