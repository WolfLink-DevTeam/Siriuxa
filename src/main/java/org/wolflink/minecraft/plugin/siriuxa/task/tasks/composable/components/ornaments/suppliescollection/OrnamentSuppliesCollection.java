package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.suppliescollection;

import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.TaskOrnament;

@Singleton
public class OrnamentSuppliesCollection extends TaskOrnament {
    public OrnamentSuppliesCollection() {
        super("物资收集", "§a", "任务中的物资在任务结束后可以带回一部分。");
    }

    private boolean enabled = false;
    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }
}
