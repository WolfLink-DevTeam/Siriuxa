package org.wolflink.minecraft.plugin.siriuxa.task.abstracts;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wolflink.minecraft.plugin.siriuxa.task.regions.EvacuationZone;
import org.wolflink.minecraft.plugin.siriuxa.task.regions.SpawnZone;
import org.wolflink.minecraft.plugin.siriuxa.task.regions.TaskArea;

@Data
public abstract class TaskRegion {
    @Nullable
    protected TaskArea taskArea;
    @Nullable
    protected EvacuationZone evacuationZone = null;
    @NotNull
    protected SpawnZone spawnZone = new SpawnZone();
    /**
     * 检查 TaskArea 是否为空
     */
    public boolean notAvailable() {
        return taskArea == null;
    }
}
