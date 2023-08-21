package org.wolflink.minecraft.plugin.siriuxa.task.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;

@Getter
@AllArgsConstructor
public class TaskEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    private final Task task;
    private final boolean result;
}
