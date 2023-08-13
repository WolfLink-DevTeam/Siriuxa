package org.wolflink.minecraft.plugin.siriuxa.task.tasks.common;

import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.database.OfflinePlayerDB;
import org.wolflink.minecraft.plugin.siriuxa.file.database.OfflinePlayerRecord;
import org.wolflink.minecraft.plugin.siriuxa.task.interfaces.ITaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.ExplorationTaskQueue;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.taskstage.EndStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.taskstage.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.taskstage.WaitStage;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeamRepository;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeamService;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;

import java.util.*;

@Singleton
public class TaskService implements ITaskService {
    private final Random random = new Random();
    private final Map<UUID, Integer> escapeTaskMap = new HashMap<>();
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private GlobalTeamRepository globalTeamRepository;
    @Inject
    private VaultAPI vaultAPI;
    @Inject
    private Config config;
    @Inject
    private TaskFactory taskFactory;
    @Inject
    private TaskRelationProxy taskRelationProxy;
    @Inject
    private ExplorationTaskQueue explorationTaskQueue;

    @Override
    public void goLobby(Player player) {
        ITaskService taskService = taskRelationProxy.getTaskService(player.getWorld().getName());
        if(taskService == null) {
            Notifier.chat("你当前所处的世界不支持返回大厅！",player);
            return;
        }
        taskService.goLobby(player);
    }

    @Override
    public void goTask(Player player, Task task) {
        ITaskService taskService = taskRelationProxy.getTaskService(task);
        taskService.goTask(player,task);
    }

    /**
     * 以单个玩家的身份创建任务
     * 如果玩家不在队伍中，会创建一个队伍
     */
    @Override
    public Result create(Player player, Class<? extends Task> taskClass, TaskDifficulty taskDifficulty) {
        GlobalTeam globalTeam = globalTeamRepository.findByPlayer(player);
        if (globalTeam == null) {
            GlobalTeamService globalTeamService = IOC.getBean(GlobalTeamService.class);
            Result result = globalTeamService.create(player);
            if (!result.result()) return result; // 队伍创建失败
            globalTeam = globalTeamRepository.findByPlayer(player);
        }
        if (globalTeam == null) return new Result(false, "玩家创建了队伍但未找到所在队伍");
        return create(globalTeam, taskClass, taskDifficulty);
    }

    /**
     * 以队伍的身份创建指定类型的任务
     * 并绑定队伍与任务
     */
    @Override
    public Result create(GlobalTeam globalTeam, Class<? extends Task> taskClass, TaskDifficulty taskDifficulty) {
        Result canCreate = explorationTaskQueue.canCreateTask();
        if(!canCreate.result())return canCreate;
        if (globalTeam.getSelectedTask() != null) return new Result(false, "当前队伍已经选择了任务，无法再次创建。");

        List<OfflinePlayer> offlinePlayers = globalTeam.getOfflinePlayers();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (!offlinePlayer.isOnline()) return new Result(false, "队伍中有离线玩家，无法创建任务。");
        }
        // 检查成员能否接受任务
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if(!canAccept(taskClass,taskDifficulty,offlinePlayer))
                return new Result(false, "队伍中至少有一名成员不满足接受任务的条件。");
        }
        // 成员接受任务
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            accept(taskClass,taskDifficulty,offlinePlayer);
        }
        Task task = taskFactory.create(taskClass, globalTeam, taskDifficulty);
        if (task != null) {
            // 与队伍绑定
            globalTeam.setSelectedTask(task);
            taskRepository.insert(task);
            return new Result(true, "任务创建成功。");
        } else {
            return new Result(false, "暂不支持的任务类型");
        }

    }

    @Override
    public Result ready(Task task) {
        if (task == null) return new Result(false, "不存在的任务。");
        if (task.getGlobalTeam().getPlayers().isEmpty()) {
            taskRepository.deleteByKey(task.getTaskUuid());
            return new Result(false, "该任务所属队伍没有任何在线玩家。");
        }
        if (task.getStageHolder().getThisStage() instanceof WaitStage) {
            Result canStart = explorationTaskQueue.canCreateTask();
            if(!canStart.result()) return canStart;
            explorationTaskQueue.taskStarted();
            task.getStageHolder().next();
            return new Result(true, "任务即将开始。");
        }
        return new Result(false, "任务当前不处于等待阶段，无法准备。");
    }

    /**
     * 玩家离线触发
     */
    public void offline(@NonNull Task task, @NonNull Player player) {
        OfflinePlayerDB offlinePlayerDB = IOC.getBean(OfflinePlayerDB.class);
        Notifier.debug("玩家" + player.getName() + "离线了。");
        // 已经有标记计时了
        if (escapeTaskMap.containsKey(player.getUniqueId())) return;
        Notifier.debug("正在尝试保存玩家" + player.getName() + "保存玩家离线数据。");
        OfflinePlayerRecord offlinePlayerRecord = new OfflinePlayerRecord(player);
        offlinePlayerDB.save(offlinePlayerRecord);
        // 如果玩家还在进行中的任务中，3分钟后都没再次登录，并且任务还在进行中或者已结束，则标记其为逃跑状态，在下次上线时触发相关方法
        Stage nowStage = task.getStageHolder().getThisStage();
        if (nowStage instanceof GameStage) {
            int taskId = Bukkit.getScheduler().runTaskLater(Siriuxa.getInstance(), () -> {
                Notifier.debug("正在判断玩家" + player.getName() + "是否从任务中逃跑。");
                Stage futureStage = task.getStageHolder().getThisStage();
                if (futureStage instanceof GameStage || futureStage instanceof EndStage) {
                    offlinePlayerRecord.setTaskEscape(true);
                    offlinePlayerDB.save(offlinePlayerRecord);
                    task.escape(player);
                    Notifier.debug("玩家" + player.getName() + "从任务中逃跑了。");
                }
            }, 20 * 60 * 3L).getTaskId();
            escapeTaskMap.put(player.getUniqueId(), taskId);
        }
    }

    /**
     * 玩家上线时触发
     */
    public void online(@NonNull Player player) {
        OfflinePlayerDB offlinePlayerDB = IOC.getBean(OfflinePlayerDB.class);
        // 清理标记计时任务
        if (escapeTaskMap.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(escapeTaskMap.get(player.getUniqueId()));
            escapeTaskMap.remove(player.getUniqueId());
        }
        OfflinePlayerRecord offlinePlayerRecord = offlinePlayerDB.load(player);
        if (offlinePlayerRecord == null) {
            Notifier.debug("没有获取到玩家" + player.getName() + "的离线数据。");
            return; // 没有离线记录数据
        }
        boolean escapeMark = offlinePlayerRecord.isTaskEscape();
        // 玩家从任务中逃跑
        if (escapeMark) {
            offlinePlayerRecord.setTaskEscape(false);
            offlinePlayerDB.save(offlinePlayerRecord);
            Notifier.debug("逃跑的玩家" + player.getName() + "再次上线了。");
            goLobby(player);
        }
    }

    /**
     * 将请求转发给对应具体任务的业务类
     */
    @Override
    public boolean canAccept(Class<? extends Task> taskClass, TaskDifficulty taskDifficulty, OfflinePlayer offlinePlayer) {
        ITaskService taskService = taskRelationProxy.getTaskService(taskClass);
        return taskService.canAccept(taskClass,taskDifficulty,offlinePlayer);
    }
    /**
     * 将请求转发给对应具体任务的业务类
     */
    @Override
    public void accept(Class<? extends Task> taskClass, TaskDifficulty taskDifficulty, OfflinePlayer offlinePlayer) {
        ITaskService taskService = taskRelationProxy.getTaskService(taskClass);
        taskService.accept(taskClass,taskDifficulty,offlinePlayer);
    }

    public Result giveUp(Task task) {
        if(task.getStageHolder().getThisStage() instanceof WaitStage) {
            task.deleteTask();
            return new Result(true,"任务已成功删除。");
        }
        if(task.getStageHolder().getThisStage() instanceof GameStage) {
            task.triggerFailed();
            return new Result(true,"在任务进行的过程中放弃了，任务失败。");
        }
        return new Result(false,"暂不支持的任务阶段："+task.getStageHolder().getThisStage().getDisplayName());
    }

    /**
     * 强制标记所有任务为完成状态
     */
    public void finishAllTask() {
        for (Task task : taskRepository.findAll()) {
            if(task.getStageHolder().getThisStage() instanceof GameStage) task.triggerFinish(true);
        }
    }
    public Result forceFinishTask(Player player) {
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if(task == null) return new Result(false,"玩家当前没有正在进行的任务。");
        if(!(task.getStageHolder().getThisStage() instanceof GameStage)) return new Result(false,"玩家任务不处于游戏阶段，无法结束。");
        task.triggerFinish();
        return new Result(true,"任务已被强制触发为完成。");
    }
    public Result forceFailedTask(Player player) {
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if(task == null) return new Result(false,"玩家当前没有正在进行的任务。");
        if(!(task.getStageHolder().getThisStage() instanceof GameStage)) return new Result(false,"玩家任务不处于游戏阶段，无法结束。");
        task.triggerFailed();
        return new Result(true,"任务已被强制触发为失败。");
    }
}
