package org.wolflink.minecraft.plugin.siriuxa.team;

import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.task.interfaces.ITaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRelationProxy;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration.taskstage.WaitStage;
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
        GlobalTeam globalTeam = new GlobalTeam(player.getUniqueId());
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
            if (!taskService.canAccept(task.getClass(), task.getTaskDifficulty(), player)) {
                return new Result(false, "当前队伍已经选择了任务，而你不满足加入任务所需条件。");
            }
            taskService.accept(task.getClass(), task.getTaskDifficulty(), player);
            globalTeam.join(player);
            Notifier.broadcastChat(globalTeam.getPlayers(), "玩家 " + player.getName() + " 加入了队伍。");
            return new Result(true, "成功加入队伍，并接受了相应的任务。");
        } else {
            globalTeam.join(player);
            Notifier.broadcastChat(globalTeam.getPlayers(), "玩家 " + player.getName() + " 加入了队伍。");
            return new Result(true, "成功加入队伍。");
        }
    }

    /**
     * 玩家主动离开队伍
     * 如果队伍已经选择了任务，则无法退出
     * 如果玩家是队长，则解散队伍
     */
    private Result leave(@NonNull OfflinePlayer offlinePlayer, @NonNull GlobalTeam globalTeam) {
        if (globalTeam.getSelectedTask() != null) return new Result(false, "队伍已经选择了任务，无法退出。");
        globalTeam.leave(offlinePlayer);
        Notifier.broadcastChat(globalTeam.getPlayers(), "玩家 " + offlinePlayer.getName() + " 离开了队伍。");
        if (globalTeam.getOwnerUuid().equals(offlinePlayer.getUniqueId())) dissolve(globalTeam);
        return new Result(true, "退出队伍成功。");
    }

    public Result leave(@NonNull OfflinePlayer offlinePlayer) {
        GlobalTeam globalTeam = globalTeamRepository.findByPlayer(offlinePlayer);
        if (globalTeam == null) return new Result(false, "你没有处在任何队伍中。");
        return leave(offlinePlayer, globalTeam);
    }

    public Result kick(@NonNull OfflinePlayer teamOwner, @NonNull String beenKickedName) {
        GlobalTeam globalTeam = globalTeamRepository.findByPlayer(teamOwner);
        if (globalTeam == null) return new Result(false, "你没有处在任何队伍中。");
        if (!globalTeam.getOwnerUuid().equals(teamOwner.getUniqueId()))
            return new Result(false, "你不是队长，无法进行此操作。");
        OfflinePlayer beenKicked = globalTeam.getOfflinePlayer(beenKickedName);
        if (beenKicked == null) return new Result(false, "未在队伍中找到名为 " + beenKickedName + " 的玩家。");
        Result kickedResult = leave(beenKicked, globalTeam);
        if (!kickedResult.result()) return new Result(false, "踢出失败：" + kickedResult.msg());
        if (beenKicked.isOnline()) Notifier.chat("你已被踢出队伍。", beenKicked.getPlayer());
        return new Result(true, "玩家 " + beenKicked.getName() + " 已从队伍中踢出。");
    }

    public Result giveUpTask(@NonNull OfflinePlayer teamOwner) {
        GlobalTeam globalTeam = globalTeamRepository.findByPlayer(teamOwner);
        if (globalTeam == null) return new Result(false, "你没有处在任何队伍中。");
        if (!globalTeam.getOwnerUuid().equals(teamOwner.getUniqueId()))
            return new Result(false, "你不是队长，无法进行此操作。");
        if (globalTeam.getSelectedTask() == null) return new Result(false, "队伍目前还没有选择任务。");
        Task task = globalTeam.getSelectedTask();
        Result result = IOC.getBean(TaskService.class).giveUp(task);
        if (!result.result()) return new Result(false, "放弃任务失败，原因：" + result.msg());
        return result;
    }

    /**
     * 解散队伍
     * 如果队伍正在进行任务则无法解散
     */
    public Result dissolve(GlobalTeam globalTeam) {
        Task task = globalTeam.getSelectedTask();
        // 当前已经选择了任务
        if (task != null) {
            if (task.getStageHolder().getThisStage() instanceof WaitStage) {
                // 尝试放弃
                Result giveupResult = giveUpTask(globalTeam.getOfflineOwner());
                // 放弃失败
                if (!giveupResult.result()) return giveupResult;
            } else return new Result(false, "任务正在进行中，无法解散队伍。");
        }
        globalTeamRepository.deleteByKey(globalTeam.getTeamUuid());
        globalTeam.getPlayers().forEach(p -> Notifier.chat("由于队长退出，队伍已自动解散。", p));
        globalTeam.clear();
        return new Result(true, "解散成功");
    }
}
