package org.wolflink.minecraft.plugin.siriuxa.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MetadataKey {
    MONSTER_BELONG_PLAYER("monster-belong-to-player"),
    MONSTER_BELONG_TASK_UUID("monster-belong-to-task"),
    HARVEST_BLOCK_PLACER("harvest-block-placer"),
    ;
    private final String key;
}
