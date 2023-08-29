package org.wolflink.minecraft.plugin.siriuxa.task.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class WheatBlockSellEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * 出售方块的玩家
     */
    private final Player player;
    /**
     * 麦穗方块
     */
    private final Block wheatBlock;
    /**
     * 最终获得的麦穗
     */
    private final double wheatValue;

}
