package priv.mikkoayaka.minecraft.plugin.siriuxa.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.wolflink.minecraft.wolfird.framework.notifier.BaseNotifier;
import priv.mikkoayaka.minecraft.plugin.siriuxa.Siriuxa;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Notifier {
    private static final BaseNotifier notifier = Siriuxa.getInstance().getNotifier();

    public static void info(String msg) {
        notifier.info(msg);
    }

    public static void warn(String msg) {
        notifier.warn(msg);
    }

    public static void error(String msg) {
        notifier.error(msg);
    }

    public static void debug(String msg) {
        notifier.debug(msg);
    }

    public static void chat(String msg, Player player) {
        notifier.chat(msg, player);
    }

    public static void notify(String msg, Player player) {
        notifier.notify(msg, player);
    }

    public static void broadcastChat(Collection<UUID> uuids, String msg) {
        uuids.stream()
                .map(Bukkit::getPlayer)
                .filter(p -> p != null && p.isOnline())
                .forEach(p -> {
                    notifier.chat(msg, p);
                });
    }

    public static void broadcastChat(List<Player> players, String msg) {
        players.forEach(p -> {
            if (!p.isOnline()) return;
            notifier.chat(msg, p);
        });
    }
}
