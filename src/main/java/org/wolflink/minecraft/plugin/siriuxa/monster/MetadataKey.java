package org.wolflink.minecraft.plugin.siriuxa.monster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MetadataKey {
    BELONG_PLAYER("belong-to-player"),
    BELONG_TASK_UUID("belong-to-task");
    private final String key;
}
