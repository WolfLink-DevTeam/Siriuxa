package org.wolflink.minecraft.plugin.siriuxa.team;

import lombok.Getter;
import lombok.Setter;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.UUID;


@Getter
public final class GlobalTeam extends Team {
    private final UUID teamUuid = UUID.randomUUID();
    /**
     * 当前选择的任务
     */
    @Setter
    @Nullable
    private Task selectedTask = null;

    public GlobalTeam(UUID ownerUuid) {
        super(ownerUuid, new HashSet<>());
    }
}
