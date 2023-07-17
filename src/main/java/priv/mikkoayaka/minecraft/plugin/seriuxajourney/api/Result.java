package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api;

import org.bukkit.entity.Player;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

public record Result(boolean result, String msg) {
    public void show(Player player) {
        Notifier.chat(msg, player);
    }
}
