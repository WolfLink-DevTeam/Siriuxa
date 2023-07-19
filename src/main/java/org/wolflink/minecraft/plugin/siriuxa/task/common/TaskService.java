package org.wolflink.minecraft.plugin.siriuxa.task.common;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.database.InventoryDB;
import org.wolflink.minecraft.plugin.siriuxa.file.database.OfflinePlayerDB;
import org.wolflink.minecraft.plugin.siriuxa.file.database.OfflinePlayerRecord;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.InvBackupService;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.task.common.region.TaskRegion;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.EndStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.ReadyStage;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.WaitStage;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeam;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeamRepository;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeamService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            Result result = taskTeamService.create(player);
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

    private final Map<UUID,Integer> escapeTaskMap = new HashMap<>();

    /**
     * 玩家离线触发
     */
    public void offline(@NonNull Task task,@NonNull Player player) {
        OfflinePlayerDB offlinePlayerDB = IOC.getBean(OfflinePlayerDB.class);
        // 已经有标记计时了
        if(escapeTaskMap.containsKey(player.getUniqueId())) return;
        OfflinePlayerRecord offlinePlayerRecord = new OfflinePlayerRecord(player);
        offlinePlayerDB.save(offlinePlayerRecord);
        // 如果玩家还在任务中，3分钟后标记其为逃跑状态，在下次上线时触发相关方法
        int taskId = Bukkit.getScheduler().runTaskLater(Siriuxa.getInstance(),()->{
            if(task.getStageHolder().getThisStage() instanceof GameStage) {
                offlinePlayerRecord.setTaskEscape(true);
                offlinePlayerDB.save(offlinePlayerRecord);
                task.escape(player);
            }
        },20 * 60 * 3).getTaskId();
        escapeTaskMap.put(player.getUniqueId(),taskId);
    }

    /**
     * 玩家上线时触发
     */
    public void online(@NonNull Player player) {
        OfflinePlayerDB offlinePlayerDB = IOC.getBean(OfflinePlayerDB.class);
        // 清理标记计时任务
        if(escapeTaskMap.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(escapeTaskMap.get(player.getUniqueId()));
            escapeTaskMap.remove(player.getUniqueId());
        }
        OfflinePlayerRecord offlinePlayerRecord = offlinePlayerDB.load(player);
        if(offlinePlayerRecord == null) return; // 没有离线记录数据
        boolean escapeMark = offlinePlayerRecord.isTaskEscape();
        // 玩家从任务中逃跑
        if(escapeMark) {
            offlinePlayerRecord.setTaskEscape(false);
            goLobby(player);
        }
    }

    /**
     * 回到大厅
     */
    public void goLobby(Player player) {
        InventoryDB inventoryDB = IOC.getBean(InventoryDB.class);
        PlayerBackpack mainInv = inventoryDB.loadMain(player);
        if(mainInv == null) {
            Notifier.error("未能找到玩家"+player.getName()+"的主背包数据。");
            return;
        }
        PlayerBackpack.getEmptyBackpack().apply(player);
        mainInv.apply(player);
        // 传送回城
        player.teleport(config.getLobbyLocation());
    }

    /**
     * 前往任务地点
     */
    public void goTask(Player player,Task task) {
        TaskRegion taskRegion = task.getTaskRegion();
        if(taskRegion == null) {
            Notifier.error("任务区域为空，玩家无法进入任务区域！");
            return;
        }
        InvBackupService invBackupService = IOC.getBean(InvBackupService.class);
        // 保存玩家背包信息
        invBackupService.saveMainInv(player);
        // 应用空背包信息
        PlayerBackpack.getEmptyBackpack().apply(player);
        // 传送到指定方块上
        List<Location> spawnLocations = task.getBeaconLocations();
        if(spawnLocations.size() == 0) player.teleport(task.getTaskRegion().getCenter());
        else player.teleport(spawnLocations.get((int) (Math.random() * spawnLocations.size())));
    }
}
