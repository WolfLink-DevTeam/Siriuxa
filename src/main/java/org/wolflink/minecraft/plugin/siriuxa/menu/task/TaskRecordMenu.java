package org.wolflink.minecraft.plugin.siriuxa.menu.task;

import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Menu;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerTaskRecord;
import org.wolflink.minecraft.plugin.siriuxa.file.database.TaskRecordDB;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.icon.TaskRecordIcon;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * 任务记录菜单
 */
public class TaskRecordMenu extends Menu {
    /**
     * 刷新周期设置小于0则为静态菜单
     * 静态菜单只会在打开时刷新一次
     */
    public TaskRecordMenu(UUID ownerUuid) {
        super(ownerUuid, -1, "§0§l任务记录", 54);
    }

    @Override
    protected void overrideIcons() {
        int startIndex = 10;
        List<PlayerTaskRecord> recordList = IOC.getBean(TaskRecordDB.class).loadRecords(getOfflineOwner().getName());
        recordList.sort((r1,r2) -> ((int)(r1.getFinishedTimeInMills()-r2.getFinishedTimeInMills())/1000));
        int maxLen = Math.min(recordList.size(), 28);
        for (int i = 0; i < maxLen; i++) {
            PlayerTaskRecord playerTaskRecord = recordList.get(i);
            setIcon(startIndex+i,new TaskRecordIcon(playerTaskRecord));
            if((i+1)%7 == 0)startIndex += 2;
        }
    }
}
