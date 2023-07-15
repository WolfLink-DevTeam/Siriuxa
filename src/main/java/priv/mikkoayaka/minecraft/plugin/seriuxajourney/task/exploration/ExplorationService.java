package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.wolfird.framework.notifier.BaseNotifier;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.Result;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.VaultAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.ExplorationDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Config;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.ConfigProjection;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.TaskRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.region.SquareRegion;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.GameStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.ReadyStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage.WaitStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.team.TaskTeam;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.team.TaskTeamRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.team.TaskTeamService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import java.util.List;

@Singleton
public class ExplorationService {

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

    public Result createTask(Player player,ExplorationDifficulty explorationDifficulty) {
        TaskTeam taskTeam = taskTeamRepository.findByPlayer(player);
        if(taskTeam == null) {
            Result result = taskTeamService.createTeam(player);
            if(!result.result()) return result; // 队伍创建失败
            taskTeam = taskTeamRepository.findByPlayer(player);
        }
        if(taskTeam == null) return new Result(false,"玩家创建了队伍但未找到所在队伍");
        return createTask(taskTeam,explorationDifficulty);
    }
    public Result createTask(TaskTeam taskTeam, ExplorationDifficulty explorationDifficulty) {
        if(taskTeam.getSelectedTask() != null) return new Result(false,"当前队伍已经选择了任务，无法再次创建。");
        double cost = explorationDifficulty.getWheatCost();
        List<OfflinePlayer> offlinePlayers = taskTeam.getOfflinePlayers();
        // 检查成员余额
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if(vaultAPI.getEconomy(offlinePlayer) < cost) return new Result(false,"队伍中至少有一名成员无法支付本次任务费用。");
        }
        // 成员支付任务成本
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            vaultAPI.takeEconomy(offlinePlayer,cost);
        }
        // 创建任务
        ExplorationTask task = new ExplorationTask(taskTeam,explorationDifficulty);
        // 与队伍绑定
        taskTeam.setSelectedTask(task);
        taskRepository.insert(task);
        return new Result(true,"任务登记完成。");
    }
    public Result readyTask(ExplorationTask explorationTask) {
        if(explorationTask == null)return new Result(false,"不存在的任务。");
        if(explorationTask.getPlayers().size() == 0) {
            taskRepository.deleteByKey(explorationTask.getTaskUuid());
            return new Result(false,"该任务没有任何在线玩家。");
        }
        if(explorationTask.getStageHolder().getThisStage() instanceof WaitStage) {
            explorationTask.getStageHolder().next();
            return new Result(true,"任务即将开始。");
        }
        return new Result(false,"任务当前不处于等待阶段，无法准备。");
    }

    /**
     * 删除任务，通知并解绑相关队伍
     *
     * 任务如果在进行准备，进行阶段，结束阶段，会将所有玩家传送到大厅
     */
    public Result deleteTask(ExplorationTask explorationTask) {
        TaskTeam taskTeam = explorationTask.getTaskTeam();
        if(taskTeam != null) {
            Notifier.broadcastChat(taskTeam.getPlayers(),"§e队伍所选任务已被删除，请重新选择。");
            Stage stage = explorationTask.getStageHolder().getThisStage();
            if(!(stage instanceof WaitStage)) {
                for (Player player : taskTeam.getPlayers()) {
                    player.teleport(config.getLobbyLocation());
                }
            }
            taskTeam.setSelectedTask(null);
            explorationTask.setTaskTeam(null);
        }
        if(explorationTask.getStageHolder().getThisStage() instanceof GameStage) {
            explorationTask.failed();
        }
        taskRepository.deleteByKey(explorationTask.getTaskUuid());
        return new Result(true,"任务删除成功。");
    }
}
