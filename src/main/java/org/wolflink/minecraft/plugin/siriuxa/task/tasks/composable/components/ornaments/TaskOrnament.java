package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.wolflink.minecraft.plugin.siriuxa.api.INameable;
import org.wolflink.minecraft.plugin.siriuxa.api.IStatus;

@Getter
@AllArgsConstructor
public abstract class TaskOrnament implements IStatus, INameable {

    String name;
    String color;
    String description;

}
