package org.wolflink.minecraft.plugin.siriuxa.team;

import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.WaitStage;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;

import java.util.Objects;

@Singleton
public class TaskTeamService {

    @Inject
    private TaskTeamRepository taskTeamRepository;

    @Inject
    private Config config;

    /**
     * 创建一个队伍
     */
    public Result create(Player player) {
        if (taskTeamRepository.findByPlayer(player) != null)
            return new Result(false, "你当前已处于其它队伍中，无法创建队伍。");
        TaskTeam taskTeam = new TaskTeam();
        taskTeam.join(player);
        taskTeamRepository.insert(taskTeam);
        return new Result(true, "队伍创建成功。");
    }

    /**
     * 解散队伍
     * 队伍的任务也会删除
     */
    public Result delete(TaskTeam taskTeam) {
        taskTeam.clear();
        Task task = taskTeam.getSelectedTask();
        if (task != null) {
            TaskService taskService = IOC.getBean(TaskService.class);
            taskService.delete(task);
        }
        taskTeamRepository.deleteByKey(taskTeam.getTeamUuid());
        return new Result(true, "队伍已解散。");
    }

    /**
     * 加入队伍
     */
    public Result join(Player player, TaskTeam taskTeam) {
        if (taskTeamRepository.findByPlayer(player) != null) {
            return new Result(false, "玩家当前已处在其他队伍中，不允许加入。");
        }
        Task task = taskTeam.getSelectedTask();
        if (task != null) {
            return IOC.getBean(TaskService.class).join(task,player);
        } else {
            taskTeam.join(player);
            return new Result(true, "加入成功");
        }
    }
    /**
     * 离开队伍，同时也会离开当前任务(如果任务在进行中则算作逃跑)
     */
    public Result leave(@NonNull OfflinePlayer offlinePlayer,@NonNull TaskTeam taskTeam) {
        Task task = taskTeam.getSelectedTask();
        // 玩家处在任务中
        if (task != null) {
            TaskService taskService = IOC.getBean(TaskService.class);
            Result result = taskService.leave(task,offlinePlayer);
            if (!result.result()) return result;
        }
        taskTeam.leave(offlinePlayer.getUniqueId());
        return new Result(true, "队伍退出成功。");
    }
    public Result leave(@NonNull OfflinePlayer offlinePlayer) {
        TaskTeam taskTeam = taskTeamRepository.findByPlayerUuid(offlinePlayer.getUniqueId());
        if (taskTeam == null) return new Result(false, "你没有在队伍中。");
        return leave(offlinePlayer,taskTeam);
    }

    /**
     * 将指定前缀的玩家从队伍中踢出
     * TODO 投票踢人
     */
    public Result kick(Player player, String kickedPrefix) {
        TaskTeam taskTeam = taskTeamRepository.findByPlayer(player);
        if (taskTeam == null) return new Result(false, "你没有处在队伍中。");
        for (OfflinePlayer offlinePlayer : taskTeam.getOfflinePlayers()) {
            if (Objects.requireNonNull(offlinePlayer.getName()).startsWith(kickedPrefix)) {
                if (player.getName().equals(offlinePlayer.getName())) return new Result(false, "你不能踢出你自己。");
                leave(offlinePlayer);
                return new Result(true, "踢出成功。");
            }
        }
        return new Result(false, "未在队伍中找到名字以" + kickedPrefix + "开头的玩家。");
    }

    /**
     * 判断玩家是否处于队伍中
     */
    public boolean isInTeam(Player player) {
        TaskTeam taskTeam = taskTeamRepository.findByPlayer(player);
        return taskTeam != null;
    }
}
