package org.wolflink.minecraft.plugin.siriuxa.menu.task;

import lombok.Getter;
import lombok.Setter;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Menu;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerTaskRecord;
import org.wolflink.minecraft.plugin.siriuxa.file.database.TaskRecordDB;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.icon.NextPage;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.icon.PreviousPage;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.icon.TaskRecordIcon;

import java.util.ArrayList;
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

    List<PlayerTaskRecord> totalRecordList = new ArrayList<>();
    @Getter
    private int page = 1;

    public void setPage(int value) {
        this.page = value;
        super.refresh();
    }

    @Override
    protected void overrideIcons() {
        setIcon(45,new PreviousPage(this));
        setIcon(53,new NextPage(this));
        int startIndex = 10;
        totalRecordList = IOC.getBean(TaskRecordDB.class).loadRecords(getOfflineOwner().getName());
        totalRecordList.sort((r1,r2) -> ((int)(r2.getFinishedTimeInMills()-r1.getFinishedTimeInMills())/1000));
        int recordSize = totalRecordList.size();

        // 闭区间 [pageFirstRecord,pageLastRecord]
        int pageFirstRecord = page * 28 - 28;
        int pageLastRecord = pageFirstRecord + 27;

        if(pageFirstRecord >= recordSize) return;

        List<PlayerTaskRecord> pageRecordList = totalRecordList.subList(pageFirstRecord,Math.min(pageLastRecord+1,recordSize));
        for (int i = 0; i < pageRecordList.size(); i++) {
            PlayerTaskRecord playerTaskRecord = pageRecordList.get(i);
            setIcon(startIndex+i,new TaskRecordIcon(playerTaskRecord));
            if((i+1)%7 == 0)startIndex += 2;
        }
    }
    public boolean hasPreviousPage() {
        return page > 1;
    }
    public boolean hasNextPage() {
        return page * 28 < totalRecordList.size();
    }
}
