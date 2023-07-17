package priv.mikkoayaka.minecraft.plugin.siriuxa.task.common;

import lombok.Data;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

@Data
public class TaskStat {

    private final Task task;
    private final SubScheduler subScheduler = new SubScheduler();

    private double lastWheat = 0;
    private double nowWheat = 0;

    private boolean started = false;

    public double getWheatChange() {
        return nowWheat - lastWheat;
    }

    public TaskStat(Task task) {
        this.task = task;
    }

    public void start() {
        if (started) return;
        subScheduler.runTaskTimerAsync(() -> {
            lastWheat = nowWheat;
            nowWheat = task.getTaskWheat();
        }, 20, 20);
    }

    public void stop() {
        if (!started) return;
        subScheduler.cancelAllTasks();
    }
}
