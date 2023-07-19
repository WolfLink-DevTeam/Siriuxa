package org.wolflink.minecraft.plugin.siriuxa.task.common;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.INameable;
import org.wolflink.minecraft.plugin.siriuxa.api.world.BlockAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationCommandSender;
import org.wolflink.minecraft.plugin.siriuxa.api.world.WorldEditAPI;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.database.OfflinePlayerDB;
import org.wolflink.minecraft.plugin.siriuxa.file.database.OfflinePlayerRecord;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerTaskRecord;
import org.wolflink.minecraft.plugin.siriuxa.file.database.TaskRecordDB;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.monster.TaskMonsterSpawner;
import org.wolflink.minecraft.plugin.siriuxa.task.common.region.TaskRegion;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeam;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeamRepository;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.StageHolder;

import java.util.*;

/**
 * 抽象任务类
 * 接受需要支付麦穗
 * 麦穗会流失
 */
@Data
public abstract class Task implements INameable {

    /**
     * 任务开始时间
     */
    private Calendar startTime;
    /**
     * 任务数据统计类(纯异步)
     */
    private final TaskStat taskStat = new TaskStat(this);
    /**
     * 任务UUID
     */
    private final UUID taskUuid = UUID.randomUUID();
    /**
     * 基础麦穗流失量(每秒)
     */
    private final double baseWheatLoss;
    /**
     * 麦穗流失倍率
     */
    private double wheatLossMultiple = 1.0;
    /**
     * 麦穗流失加速度
     */
    private final double wheatLostAcceleratedSpeed;
    /**
     * 本次任务的麦穗余量
     */
    private double taskWheat = 0;
    /**
     * 参与本次任务的玩家
     */
    private Set<UUID> playerUuids;
    @Nullable
    private TaskRegion taskRegion = null;

    @NonNull
    private final TaskDifficulty taskDifficulty;

    /**
     * 当前可用的撤离点
     */
    private EvacuationZone availableEvacuationZone = null;

    @Getter
    private final StageHolder stageHolder;

    private UUID teamUuid = null;

    protected abstract StageHolder initStageHolder();

    private final TaskMonsterSpawner taskMonsterSpawner;

    public Task(TaskTeam taskTeam, TaskDifficulty taskDifficulty) {
        this.teamUuid = taskTeam.getTeamUuid();
        this.playerUuids = taskTeam.getMemberUuids();
        this.taskDifficulty = taskDifficulty;
        this.taskMonsterSpawner = new TaskMonsterSpawner(this);
        this.wheatLostAcceleratedSpeed = taskDifficulty.getWheatLostAcceleratedSpeed();
        this.baseWheatLoss = taskDifficulty.getBaseWheatLoss();
        stageHolder = initStageHolder();
    }

    public List<OfflinePlayer> getOfflinePlayers() {
        return playerUuids.stream()
                .map(Bukkit::getOfflinePlayer)
                .toList();
    }
    public int size() {
        return playerUuids.size();
    }
    public boolean contains(UUID uuid) {
        return playerUuids.contains(uuid);
    }
    public boolean contains(Player player) {
        return contains(player.getUniqueId());
    }

    public void addWheat(double wheat) {
        taskWheat += wheat;
    }

    public void takeWheat(double wheat) {
        taskWheat -= wheat;
        if (taskWheat <= 0) {
            taskWheat = 0;
            triggerFailed();
        }
    }

    public void takeWheat(double wheat, String reason) {
        takeWheat(wheat);
        Notifier.broadcastChat(getPlayers(), "本次任务损失了 " + wheat + " 麦穗，原因是" + reason);
    }

    /**
     * 获取该任务中的所有在线玩家
     */
    public List<Player> getPlayers() {
        return playerUuids.stream()
                .map(Bukkit::getPlayer)
                .filter(p -> p != null && p.isOnline())
                .toList();
    }

    public void addWheatLossMultiple(double value) {
        wheatLossMultiple += value;
    }

    public Set<Player> waitForEvacuatePlayers() {
        if (availableEvacuationZone == null) return new HashSet<>();
        else return availableEvacuationZone.getPlayerInZone();
    }

    private int finishCheckTaskId = -1;

    private void triggerFailed() {
        getPlayers().forEach(this::fillRecord);
        stageHolder.next();
        stopCheck();
        finishRecord();
        for (Player player : getPlayers()) {
            IOC.getBean(TaskService.class).goLobby(player);
            player.sendTitle("§c任务失败", "§7真可惜...下次再尝试吧", 10, 80, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.8f);
        }
        failed();
        deleteTask();
    }

    private void triggerFinish() {
        getPlayers().forEach(this::fillRecord);
        stageHolder.next();
        stopCheck();
        finishRecord();
        for (Player player : getPlayers()) {
            IOC.getBean(TaskService.class).goLobby(player);
            player.sendTitle("§a任务完成", "§7前往领取本次任务的报酬吧", 10, 80, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
        }
        finish();
        deleteTask();
    }

    /**
     * 游戏结束检查
     * 如果本次任务玩家数为0则意味着所有玩家逃跑/离线，宣布任务失败
     * 如果撤离玩家数和任务玩家数一致，则任务完成
     */
    public void startGameOverCheck() {
        finishCheckTaskId = Bukkit.getScheduler().runTaskTimer(Siriuxa.getInstance(), () -> {
            if (size() == 0) {
                triggerFailed();
                return;
            }
            if (waitForEvacuatePlayers().size() == size()) {
                triggerFinish();
            }
        }, 20, 20).getTaskId();
    }

    public void stopFinishCheck() {
        if (finishCheckTaskId != -1) {
            Bukkit.getScheduler().cancelTask(finishCheckTaskId);
            finishCheckTaskId = -1;
        }
    }

    private int evacuateTaskId = -1;

    /**
     * 停止生成撤离点
     */
    public void stopEvacuateTask() {
        if (evacuateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(evacuateTaskId);
            evacuateTaskId = -1;
            availableEvacuationZone = null;
        }
    }
    private List<Location> beaconLocations = new ArrayList<>();
    public void start(TaskRegion taskRegion) {
        this.taskRegion = taskRegion;
        initRecord();
        taskStat.start();
        startTime = Calendar.getInstance();
        this.taskWheat = size() * (taskDifficulty.getWheatCost() + taskDifficulty.getWheatSupply());
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {
            IOC.getBean(WorldEditAPI.class).pasteWorkingUnit(new LocationCommandSender(taskRegion.getCenter().clone().add(0, 2, 0)));
            beaconLocations = IOC.getBean(BlockAPI.class).searchBlock(Material.BEACON, taskRegion.getCenter(), 30);
            Bukkit.getScheduler().runTask(Siriuxa.getInstance(), () -> {
                List<Player> playerList = getPlayers();
                for (Player player : playerList) {
                    IOC.getBean(TaskService.class).goTask(player,this);
                }
                startGameOverCheck();
                startTiming();
                startEvacuateTask();
                taskRegion.startCheck();
                taskMonsterSpawner.setEnabled(true);
            });
        });
    }

    private void startEvacuateTask() {
        evacuateTaskId = Bukkit.getScheduler().runTaskTimer(Siriuxa.getInstance(), () -> {
            if (taskRegion == null) return;
            Location evacuateLocation = taskRegion.getEvacuateLocation((int) taskRegion.getRadius());
            if (evacuateLocation == null) {
                triggerFailed();
                return;
            }
            if (availableEvacuationZone != null) {
                availableEvacuationZone.setAvailable(false);
                availableEvacuationZone = null;
            } else {
                availableEvacuationZone = new EvacuationZone(this,evacuateLocation.getWorld(),evacuateLocation.getBlockX(),evacuateLocation.getBlockZ(), 5);
                availableEvacuationZone.setAvailable(true);
            }
            //TODO 30|15
        }, 20 * 60, 20 * 60).getTaskId();
    }

    /**
     * 获取当前麦穗每秒流失量
     */
    public double getWheatLossPerSecNow() {
        return baseWheatLoss * wheatLossMultiple * size();
    }

    int timingTask1Id = -1;
    int timingTask2Id = -1;

    private void startTiming() {
        timingTask1Id =
                Bukkit.getScheduler().runTaskTimer(Siriuxa.getInstance(),
                        () -> takeWheat(getWheatLossPerSecNow())
                        , 20, 20).getTaskId();
        timingTask2Id =
                Bukkit.getScheduler().runTaskTimer(Siriuxa.getInstance(),
                        () -> addWheatLossMultiple(wheatLostAcceleratedSpeed)
                        , 20 * 60 * 5, 20 * 60 * 5).getTaskId();
    }

    private void stopTiming() {
        if (timingTask1Id != -1) Bukkit.getScheduler().cancelTask(timingTask1Id);
        if (timingTask2Id != -1) Bukkit.getScheduler().cancelTask(timingTask2Id);
    }

    private void stopCheck() {
        taskMonsterSpawner.setEnabled(false);
        taskStat.stop();
        stopTiming();
        if (taskRegion != null) {
            taskRegion.stopCheck();
            taskRegion = null;
        }
        stopEvacuateTask();
        stopFinishCheck();
    }

    /**
     * 任务玩家全部撤离时任务完成
     */
    protected abstract void finish();

    /**
     * 麦穗为0，或玩家全部逃跑时，任务失败
     */
    public abstract void failed();

    /**
     * 是否允许其他玩家加入
     */
    public abstract boolean canJoin();

    /**
     * 清理本次任务
     * 在任务完成/失败后调用
     */
    protected void deleteTask() {
        TaskTeam taskTeam = IOC.getBean(TaskTeamRepository.class).find(teamUuid);
        if(taskTeam != null) taskTeam.setSelectedTask(null);
        teamUuid = null;
        playerUuids.clear();
        IOC.getBean(TaskRepository.class).deleteByKey(taskUuid);
//        IOC.getBean(TaskService.class).delete(this);
    }


    private final Map<UUID, PlayerTaskRecord> playerRecordMap = new HashMap<>();

    /**
     * 初始化任务快照
     */
    private void initRecord() {
        for (UUID uuid : playerUuids) {
            PlayerTaskRecord record = new PlayerTaskRecord(uuid,this);
            playerRecordMap.put(uuid, record);
        }
    }

    /**
     * 填充玩家任务快照
     * TODO 在单个玩家撤离时调用
     */
    private void fillRecord(OfflinePlayer offlinePlayer) {
        PlayerTaskRecord record = playerRecordMap.get(offlinePlayer.getUniqueId());
        if (record == null) {
            Notifier.error("在尝试补充任务记录数据时，未找到玩家"+offlinePlayer.getName()+"的任务记录类。");
            return;
        }
        PlayerBackpack playerBackpack;
        Player player = offlinePlayer.getPlayer();
        if(player == null || !player.isOnline()) {
            OfflinePlayerRecord offlinePlayerRecord = IOC.getBean(OfflinePlayerDB.class).load(offlinePlayer);
            if(offlinePlayerRecord == null) {
                Notifier.error("在尝试补充任务记录数据时，未找到离线玩家"+offlinePlayer.getName()+"的离线缓存数据。");
                return;
            }
            playerBackpack = offlinePlayerRecord.getPlayerBackpack();
            record.setEscape(true); //标记玩家逃跑
        } else playerBackpack = new PlayerBackpack(player);
        record.setPlayerBackpack(playerBackpack); // 保存玩家背包到任务记录中
    }

    /**
     * 完成任务快照，并保存到本地
     * 在任务结束阶段调用
     */
    private void finishRecord() {
        TaskRecordDB taskRecordDB = IOC.getBean(TaskRecordDB.class);
        for (PlayerTaskRecord playerTaskRecord : playerRecordMap.values()) {
            long nowMills = Calendar.getInstance().getTimeInMillis();
            playerTaskRecord.setUsingTimeInMills(nowMills - startTime.getTimeInMillis());
            playerTaskRecord.setFinishedTimeInMills(nowMills);
            taskRecordDB.saveRecord(playerTaskRecord);
        }
    }

    /**
     * 撤离玩家
     * (适用于只有部分玩家乘坐撤离飞艇的情况)
     */
    public void evacuate(Player player) {
        playerUuids.remove(player.getUniqueId());
        Notifier.broadcastChat(playerUuids,"玩家"+player.getName()+"已乘坐飞艇撤离。");
        player.teleport(IOC.getBean(Config.class).getLobbyLocation());
    }

    /**
     * 玩家逃跑
     * (适用于任务过程中玩家非正常离开任务的情况)
     */
    public void escape(OfflinePlayer offlinePlayer) {
        fillRecord(offlinePlayer);
        playerUuids.remove(offlinePlayer.getUniqueId());
        Notifier.debug("玩家"+offlinePlayer.getName()+"在任务过程中失踪了。");
        Notifier.broadcastChat(playerUuids,"玩家"+offlinePlayer.getName()+"在任务过程中失踪了。");
    }
}
