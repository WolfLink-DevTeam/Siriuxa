package org.wolflink.minecraft.plugin.siriuxa.task.tasks.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.wolflink.minecraft.plugin.siriuxa.task.ornaments.OrnamentType;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum TaskProperties {
    EXPLORATION("§f", "自由勘探",
            Stream.of(OrnamentType.SAFE_WORKING,
                    OrnamentType.SCULK_INFECTION,
                    OrnamentType.SUPPLIES_COLLECTION).collect(Collectors.toSet()))
    ;
    private final String color;
    private final String taskName;
    private final Set<OrnamentType> ornamentTypes;
}
