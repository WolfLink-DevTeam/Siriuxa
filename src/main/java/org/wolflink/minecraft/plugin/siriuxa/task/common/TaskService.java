package org.wolflink.minecraft.plugin.siriuxa.task.common;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.ReadyStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.WaitStage;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeam;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeamRepository;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeamService;

import java.util.List;

@Singleton
public class TaskService {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private TaskTeamRepository taskTeamRepository;
    @Inject
    private VaultAPI vaultAPI;
    @Inject
    private Config config;
    @Inject
    private TaskFactory taskFactory;

    public TaskService() {
    }

    /**
     * 以单个玩家的身份创建任务
     * 如果玩家不在队伍中，会创建一个队伍
     */
    public Result create(Player player, Class<? extends Task> taskClass, TaskDifficulty taskDifficulty) {
        TaskTeam taskTeam = taskTeamRepository.findByPlayer(player);
        if (taskTeam == null) {
            TaskTeamService taskTeamService = IOC.getBean(TaskTeamService.class);
            Result result = taskTeamService.createTeam(player);
            if (!result.result()) return result; // 队伍创建失败
            taskTeam = taskTeamRepository.findByPlayer(player);
        }
        if (taskTeam == null) return new Result(false, "玩家创建了队伍但未找到所在队伍");
        return create(taskTeam, taskClass, taskDifficulty);
    }

    /**
     * 以队伍的身份创建指定类型的任务
     * 并绑定队伍与任务
     */
    public Result create(TaskTeam taskTeam, Class<? extends Task> taskClass, TaskDifficulty taskDifficulty) {
        if (taskTeam.getSelectedTask() != null) return new Result(false, "当前队伍已经选择了任务，无法再次创建。");
        double cost = taskDifficulty.getWheatCost();
        List<OfflinePlayer> offlinePlayers = taskTeam.getOfflinePlayers();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (!offlinePlayer.isOnline()) return new Result(false, "队伍中有离线玩家，无法创建任务。");
        }
        // 检查成员余额
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (vaultAPI.getEconomy(offlinePlayer) < cost)
                return new Result(false, "队伍中至少有一名成员无法支付本次任务费用。");
        }
        // 成员支付任务成本
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            vaultAPI.takeEconomy(offlinePlayer, cost);
        }
        Task task = taskFactory.create(taskClass, taskTeam, taskDifficulty);
        if(task != null) {
            // 与队伍绑定
            taskTeam.setSelectedTask(task);
            taskRepository.insert(task);
            return new Result(true,"任务创建成功。");
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
    public Result leave(Task task,OfflinePlayer offlinePlayer) {
        if(!task.getTaskTeam().getOfflinePlayers().contains(offlinePlayer)) return new Result(false,"你没有参与该任务，无法离开。");
        // 该玩家是参与任务的最后一人
        if (task.getTaskTeam().size() == 1) {
            // 如果在游戏阶段则立刻中止
            if (task.getStageHolder().getThisStage() instanceof GameStage) {
                task.setTaskWheat(0);
                return new Result(true,"你已成功离开该任务，但因此任务失败了。");
            }
            taskRepository.deleteByKey(task.getTaskUuid());
        }
        Stage stage = task.getStageHolder().getThisStage();
        int wheatCost = task.getTaskDifficulty().getWheatCost();
        int wheatSupply = task.getTaskDifficulty().getWheatSupply();
        // 退回麦穗
        if (stage instanceof WaitStage || stage instanceof ReadyStage) {
            vaultAPI.addEconomy(offlinePlayer, wheatCost);
        } else {
            // 任务中扣除其一半麦穗
            task.takeWheat(wheatCost + wheatSupply, "玩家 " + offlinePlayer.getName() + " 中途退出了，Ta的麦穗也随风而逝。");
        }
        return new Result(true,"你已成功离开该任务。");
    }
}
