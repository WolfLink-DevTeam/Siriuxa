package org.wolflink.minecraft.plugin.siriuxa.task.common;

import lombok.NonNull;
import org.bukkit.*;
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
import org.wolflink.minecraft.plugin.siriuxa.invbackup.InvBackupService;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.task.common.region.TaskRegion;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.EndStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.WaitStage;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeamRepository;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeamService;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;

import java.util.*;

@Singleton
public class TaskService {
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

    public TaskService() {
    }

    /**
     * 以单个玩家的身份创建任务
     * 如果玩家不在队伍中，会创建一个队伍
     */
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
    public Result create(GlobalTeam globalTeam, Class<? extends Task> taskClass, TaskDifficulty taskDifficulty) {
        if (globalTeam.getSelectedTask() != null) return new Result(false, "当前队伍已经选择了任务，无法再次创建。");
        double cost = taskDifficulty.getWheatCost();
        List<OfflinePlayer> offlinePlayers = globalTeam.getOfflinePlayers();
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

    public Result ready(Task task) {
        if (task == null) return new Result(false, "不存在的任务。");
        if (task.getGlobalTeam().getPlayers().isEmpty()) {
            taskRepository.deleteByKey(task.getTaskUuid());
            return new Result(false, "该任务所属队伍没有任何在线玩家。");
        }
        if (task.getStageHolder().getThisStage() instanceof WaitStage) {
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
     * 回到大厅(读取主背包后清理主背包数据)
     */
    public void goLobby(Player player) {
        InvBackupService invBackupService = IOC.getBean(InvBackupService.class);
        invBackupService.applyInv(player, PlayerBackpack.getEmptyBackpack());
//        Result r = invBackupService.applyMainInv(player);
//        if(!r.result()) return;
//        invBackupService.saveMainInv(player,PlayerBackpack.getEmptyBackpack());
        // 传送回城
        player.teleport(config.getLobbyLocation());
        if (!player.isOp()) player.setGameMode(GameMode.SURVIVAL);
    }

    /**
     * 前往任务地点
     */
    public void goTask(Player player, Task task) {
        TaskRegion taskRegion = task.getTaskRegion();
        if (taskRegion == null) {
            Notifier.error("任务区域为空，玩家无法进入任务区域！");
            return;
        }
//        InvBackupService invBackupService = IOC.getBean(InvBackupService.class);
//         保存玩家背包信息
//        invBackupService.saveMainInv(player);
        // 应用任务背包信息
//        invBackupService.applyInv(player,task.getDefaultKit());
        // 传送到指定方块上
        List<Location> spawnLocations = task.getBeaconLocations();
        if (spawnLocations.isEmpty()) player.teleport(task.getTaskRegion().getCenter());
        else {
            Location location = spawnLocations.get(random.nextInt(spawnLocations.size())).clone().add(0.5, 1, 0.5);
            location.getBlock().setType(Material.AIR);
            location.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
            player.teleport(location);
        }
        if (!player.isOp()) player.setGameMode(GameMode.SURVIVAL);
    }
}
