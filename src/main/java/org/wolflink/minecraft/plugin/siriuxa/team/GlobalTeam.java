package org.wolflink.minecraft.plugin.siriuxa.team;

import lombok.Getter;
import lombok.Setter;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;


public final class GlobalTeam extends Team {
    @Getter
    private final UUID teamUuid = UUID.randomUUID();
    /**
     * 当前选择的任务
     */
    @Setter
    @Getter
    @Nullable
    private Task selectedTask = null;

    public GlobalTeam() {
        super(new HashSet<>());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GlobalTeam other)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return Objects.equals(teamUuid, other.teamUuid) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamUuid) + 31 * super.hashCode();
    }

}
