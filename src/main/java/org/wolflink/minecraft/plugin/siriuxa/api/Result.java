package org.wolflink.minecraft.plugin.siriuxa.api;

import org.bukkit.entity.Player;

public record Result(boolean result, String msg) {
    public void show(Player player) {
        Notifier.chat(msg, player);
    }
}
