package org.wolflink.minecraft.plugin.siriuxa.task.tasks.lumen;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.backpack.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.LumenTaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.file.database.*;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskLumenLeftNotifyEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration.taskstage.EndStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration.taskstage.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration.taskstage.ReadyStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration.taskstage.WaitStage;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.LinearStageHolder;
import org.wolflink.minecraft.wolfird.framework.gamestage.stageholder.StageHolder;

import java.util.UUID;

/**
 * 麦穗任务
 * 任务开局会给一些初始麦穗
 * 完成任务的方式有所不同
 * 麦穗归零/玩家人数为0，任务失败
 */
public abstract class LumenTask extends Task {
    /**
     * 光体流失倍率
     */
    protected double lumenLossMultiple = 1.0;
    /**
     * 本次任务的光体余量
     */
    @Getter
    protected double taskLumen = 0;
    protected final LumenTip lumenTip = new LumenTip();

    public void addLumenLossMultiple(double value) {
        lumenLossMultiple += value;
    }

    @Getter
    private final LinearStageHolder stageHolder = (LinearStageHolder) super.getStageHolder();
    @Getter
    private final LumenTaskDifficulty difficulty = (LumenTaskDifficulty) super.getTaskDifficulty();

    public LumenTask(GlobalTeam globalTeam, LumenTaskDifficulty difficulty) {
        super(globalTeam, difficulty);
    }

    /**
     * 填充玩家任务快照
     */
    @Override
    public void fillRecord(OfflinePlayer offlinePlayer, boolean taskResult) {
        PlayerTaskRecord record = getPlayerTaskRecord(offlinePlayer.getUniqueId());
        if (record == null) {
            Notifier.error("在尝试补充任务记录数据时，未找到玩家" + offlinePlayer.getName() + "的任务记录类。");
            return;
        }
        record.setRewardWheat(lumenTaskStat.getPlayerWheatReward(
                offlinePlayer.getUniqueId(),
                difficulty.getRewardMultiple(),
                IOC.getBean(InventoryDB.class).loadEnderBackpack(offlinePlayer).isEmpty()
        )); // 保存任务麦穗
        record.setSuccess(taskResult); // 设置任务状态
        PlayerVariableDB db = IOC.getBean(PlayerVariableDB.class);
        PlayerVariables playerVariables = db.get(offlinePlayer);
        record.setSafeSlotAmount(playerVariables.getSafeSlotAmount());
        playerVariables.setSafeSlotAmount(0);
        db.save(offlinePlayer, playerVariables);
        PlayerBackpack playerBackpack;
        Player player = offlinePlayer.getPlayer();
        if (player == null || !player.isOnline()) {
            OfflinePlayerRecord offlinePlayerRecord = IOC.getBean(OfflinePlayerDB.class).load(offlinePlayer);
            if (offlinePlayerRecord == null) {
                Notifier.error("在尝试补充任务记录数据时，未找到离线玩家" + offlinePlayer.getName() + "的离线缓存数据。");
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
    @Override
    public void finishRecord() {
        TaskRecordDB taskRecordDB = IOC.getBean(TaskRecordDB.class);
        for (PlayerTaskRecord playerTaskRecord : getPlayerTaskRecords()) {
            playerTaskRecord.setUsingTimeInMills(getTaskStat().getUsingTimeInMills());
            playerTaskRecord.setFinishedTimeInMills(getTaskStat().getEndTime().getTimeInMillis());
            taskRecordDB.saveRecord(playerTaskRecord);
        }
    }

    private final LumenTaskStat lumenTaskStat = new LumenTaskStat(this);

    @Override
    public LumenTaskStat getTaskStat() {
        return lumenTaskStat;
    }

    @Override
    public void initRecord() {
        for (UUID uuid : getTaskTeam().getMemberUuids()) {
            PlayerTaskRecord record = new PlayerTaskRecord(uuid, this);
            getPlayerRecordMap().put(uuid, record);
        }
    }

    @Override
    protected StageHolder initStageHolder() {
        TaskLinearStageHolder linearStageHolder = new TaskLinearStageHolder(this);
        linearStageHolder.bindStages(new Stage[]{new WaitStage(linearStageHolder), new ReadyStage(linearStageHolder), new GameStage(linearStageHolder), new EndStage(linearStageHolder)});
        // 进入等待阶段
        linearStageHolder.next();
        return linearStageHolder;
    }

    public void addLumen(double lumen) {
        taskLumen += lumen;
    }

    private TaskLumenLeftNotifyEvent.Status status = TaskLumenLeftNotifyEvent.Status.ENOUGH;

    public void takeLumen(double lumen) {
        taskLumen -= lumen;
        int lumenTime = lumenTaskStat.getLumenTimeLeft();
        if (0 < lumenTime && lumenTime <= 300 && status != TaskLumenLeftNotifyEvent.Status.FEW) {
            status = TaskLumenLeftNotifyEvent.Status.FEW;
            Bukkit.getPluginManager().callEvent(new TaskLumenLeftNotifyEvent(this, status));
        } else if (300 < lumenTime && lumenTime <= 600 && status != TaskLumenLeftNotifyEvent.Status.INSUFFICIENT) {
            status = TaskLumenLeftNotifyEvent.Status.INSUFFICIENT;
            Bukkit.getPluginManager().callEvent(new TaskLumenLeftNotifyEvent(this, status));
        } else if (600 < lumenTime && status != TaskLumenLeftNotifyEvent.Status.ENOUGH) {
            status = TaskLumenLeftNotifyEvent.Status.ENOUGH;
            Bukkit.getPluginManager().callEvent(new TaskLumenLeftNotifyEvent(this, status));
        }
        if (taskLumen <= 0) {
            taskLumen = 0;
            triggerFailed();
        }
    }

    public double getLumenLossPerSecNow() {
        return difficulty.getBaseLumenLoss() * lumenLossMultiple * getTaskTeam().getInitSize();
    }

    protected void startLumenTask() {
        subScheduler.runTaskTimer(() -> takeLumen(getLumenLossPerSecNow())
                , 20, 20);
        subScheduler.runTaskTimer(() -> addLumenLossMultiple(difficulty.getLumenLostAcceleratedSpeed())
                , 20 * 60 * 5L, 20 * 60 * 5L);
    }

    public double getHurtLumenCost() {
        return difficulty.getHurtLumenCost();
    }


    /**
     * 游戏结束检查
     * 如果本次任务玩家数为0则意味着所有玩家逃跑/离线，宣布任务失败
     * 如果撤离玩家数和任务玩家数一致，则任务完成
     */
    @Override
    protected void failedCheck() {
        subScheduler.runTaskTimer(() -> {
            if (getTaskTeamSize() == 0) {
                triggerFailed();
            }
        }, 20, 20);
    }
}
