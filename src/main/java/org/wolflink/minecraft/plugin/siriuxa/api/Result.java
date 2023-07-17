package org.wolflink.minecraft.plugin.siriuxa.api;

import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

public record Result(boolean result, String msg) {
    public void show(Player player) {
        Notifier.chat(msg, player);
    }
}
