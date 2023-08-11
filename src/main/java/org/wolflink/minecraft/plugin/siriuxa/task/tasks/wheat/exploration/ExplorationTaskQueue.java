package org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;

import java.util.Calendar;

/**
 * 调查任务队列，减缓服务器压力
 */
@Singleton
@Getter
public class ExplorationTaskQueue {
    // 最多允许同时游玩的数量
    private final int MAX_SIZE = 8;

    private int nowSize = 0;
    private Calendar lastStarted = null;

    /**
     * 现在是否可以开始新的任务
     */
    public Result canCreateTask() {
        // 达到上限
        if(nowSize >= MAX_SIZE) return new Result(false,"任务队列已经达到最大上限，请等待一会！");
        // 上一个刚开3分钟之内
        if(lastStarted != null) {
            long delta = (Calendar.getInstance().getTimeInMillis() - lastStarted.getTimeInMillis()) / 1000;
            if(delta < 60 * 3) return new Result(false,"上一个任务刚刚开始 "+delta+" 秒，请等待至 3 分钟后方可开始新的任务。");
        }
        return new Result(true,"可以创建任务");
    }
    public void taskStarted() {
        lastStarted = Calendar.getInstance();
        nowSize++;
    }
    public void taskEnded() {
        nowSize--;
    }
}
