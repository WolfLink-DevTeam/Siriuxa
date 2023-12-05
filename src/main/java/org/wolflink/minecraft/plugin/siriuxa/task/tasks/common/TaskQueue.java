package org.wolflink.minecraft.plugin.siriuxa.task.tasks.common;

import lombok.Getter;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;

import java.util.Calendar;

/**
 * 任务队列，减缓服务器压力
 */
@Singleton
@Getter
public class TaskQueue {

    private int nowSize = 0;
    private Calendar lastStarted = Calendar.getInstance();

    public synchronized int getMaxSize() {
        return IOC.getBean(Config.class).get(ConfigProjection.TASK_QUEUE_SIZE);
    }

    /**
     * 队列是否处于阻塞状态
     */
    public Result isBlocking() {
        int maxSize = getMaxSize();
        // 达到上限
        if (nowSize >= maxSize) return new Result(true, "任务队列已经达到最大上限，请等待一会！");
        // 上一个刚开1分钟之内
        if (lastStarted != null) {
            long delta = (Calendar.getInstance().getTimeInMillis() - lastStarted.getTimeInMillis()) / 1000;
            if (delta < 60)
                return new Result(true, "上一个任务刚刚开始 " + delta + " 秒，请等待至 60 秒后方可开始新的任务。");
        }
        return new Result(false, "可以创建任务");
    }

    public synchronized void taskStarted() {
        lastStarted = Calendar.getInstance();
        nowSize++;
    }

    public synchronized void taskEnded() {
        nowSize--;
    }
}
