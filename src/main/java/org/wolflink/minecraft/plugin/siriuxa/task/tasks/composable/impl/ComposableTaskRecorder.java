package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.impl;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.backpack.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.file.database.*;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskRecorder;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.ComposableTask;

import java.util.UUID;

public class ComposableTaskRecorder extends TaskRecorder {

    public ComposableTaskRecorder(ComposableTask task) {
        super(task);
    }
    /**
     * 初始化任务快照
     */
    @Override
    public void initRecord() {
        for (UUID uuid : task.getTaskTeam().getMemberUuids()) {
            PlayerTaskRecord record = new PlayerTaskRecord(uuid, task);
            playerRecordMap.put(uuid, record);
        }
    }
    /**
     * 填充任务快照
     *
     * @param offlinePlayer
     * @param taskResult
     */
    @Override
    public void fillRecord(OfflinePlayer offlinePlayer, boolean taskResult) {
        PlayerTaskRecord record = getPlayerTaskRecord(offlinePlayer.getUniqueId());
        if (record == null) {
            Notifier.error("在尝试补充任务记录数据时，未找到玩家" + offlinePlayer.getName() + "的任务记录类。");
            return;
        }
        record.setPlayerScore(task.getTaskStat().getPlayerScore(offlinePlayer.getUniqueId()));
//        record.setRewardWheat(task.getTaskStat().getPlayerWheatReward(
//                offlinePlayer.getUniqueId(),
//                task.getTaskDifficulty().getRewardMultiple(),
//                IOC.getBean(InventoryDB.class).loadEnderBackpack(offlinePlayer).isEmpty()
//        )); // 保存任务麦穗
        record.setSuccess(taskResult); // 设置任务状态
//        PlayerVariableDB db = IOC.getBean(PlayerVariableDB.class);
//        PlayerVariables playerVariables = db.get(offlinePlayer);
//        record.setSafeSlotAmount(playerVariables.getSafeSlotAmount());
//        playerVariables.setSafeSlotAmount(0);
//        db.save(offlinePlayer, playerVariables);
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
     * 完成任务快照
     */
    @Override
    public void finishRecord() {
        PlayerTaskRecordDB playerTaskRecordDB = IOC.getBean(PlayerTaskRecordDB.class);
        ComposableTaskRecordDB composableTaskRecordDB = IOC.getBean(ComposableTaskRecordDB.class);
        ComposableTaskRecord composableTaskRecord = composableTaskRecordDB.loadRecord(task.getTaskUuid().toString());
        if(composableTaskRecord == null) {
            composableTaskRecord = ComposableTaskRecord.from((ComposableTask) task);
        }
        composableTaskRecord.setFinishedTimeInMills(task.getTaskStat().getEndTime().getTimeInMillis());
        for (PlayerTaskRecord playerTaskRecord : getPlayerTaskRecords()) {
            playerTaskRecord.setUsingTimeInMills(task.getTaskStat().getUsingTimeInMills());
            playerTaskRecordDB.saveRecord(playerTaskRecord);
        }
    }
}
