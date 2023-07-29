package org.wolflink.minecraft.plugin.siriuxa.team;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRelationProxy;
import org.wolflink.minecraft.plugin.siriuxa.task.common.interfaces.ITaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.WaitStage;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;

@Singleton
public class GlobalTeamService {

    @Inject
    private GlobalTeamRepository globalTeamRepository;

    @Inject
    private Config config;

    @Inject
    private VaultAPI vaultAPI;

    @Inject
    private TaskRelationProxy taskRelationProxy;

    /**
     * 创建一个队伍
     */
    public Result create(Player player) {
        if (globalTeamRepository.findByPlayer(player) != null)
            return new Result(false, "你当前已处于其它队伍中，无法创建队伍。");
        GlobalTeam globalTeam = new GlobalTeam();
        globalTeam.join(player);
        globalTeamRepository.insert(globalTeam);
        return new Result(true, "队伍创建成功。");
    }

    /**
     * 加入队伍
     */
    public Result join(Player player, GlobalTeam globalTeam) {
        if (globalTeamRepository.findByPlayer(player) != null) {
            return new Result(false, "玩家当前已处在其他队伍中，不允许加入。");
        }
        Task task = globalTeam.getSelectedTask();
        if (task != null) {
            Stage stage = task.getStageHolder().getThisStage();
            if (!(stage instanceof WaitStage)) {
                return new Result(false, "当前队伍的任务状态为：" + stage.getDisplayName() + "，不允许加入。");
            }
            ITaskService taskService = taskRelationProxy.getTaskService(task);
            if (!taskService.canAccept(task.getClass(),task.getDifficulty(),player)) {
                return new Result(false, "当前队伍已经选择了任务，而你不满足加入任务所需条件。");
            }
            taskService.accept(task.getClass(),task.getDifficulty(),player);
            globalTeam.join(player);
            return new Result(true, "成功加入队伍，并接受了相应的任务。");
        } else {
            globalTeam.join(player);
            return new Result(true, "成功加入队伍。");
        }
    }

    /**
     * 玩家主动离开队伍
     * 如果队伍已经选择了任务，则无法退出
     */
    public Result leave(@NonNull Player player, @NonNull GlobalTeam globalTeam) {
        if (globalTeam.getSelectedTask() != null) return new Result(false, "队伍已经选择了任务，无法退出。");
        globalTeam.leave(player);
        return new Result(true, "退出队伍成功。");
    }

    public Result leave(@NonNull Player player) {
        GlobalTeam globalTeam = globalTeamRepository.findByPlayer(player);
        if (globalTeam == null) return new Result(false, "你没有处在任何队伍中。");
        return leave(player, globalTeam);
    }
}
