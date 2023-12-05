package org.wolflink.minecraft.plugin.siriuxa.task.abstracts;

public abstract class TaskLifeCycle {
    public abstract boolean isFinished();
    public abstract boolean isFailed();
    public abstract boolean isTaskOver();
    public abstract void triggerFailed();
    public abstract void triggerFinish(boolean isServerClosing);
    public abstract void failedCheck();
    public abstract void finishedCheck();
    public abstract void start();
    public abstract void failed();
    public abstract void finish();
}
