package org.wolflink.minecraft.plugin.siriuxa.api;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.wolfird.framework.notifier.BaseNotifier;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Notifier {
    private static final BaseNotifier notifier = Siriuxa.getInstance().getNotifier();
    private static final Map<UUID, BossBar> barMap = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> barTaskMap = new ConcurrentHashMap<>();

    private Notifier() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

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

    public static void msg(CommandSender sender, String msg) {
        if(sender instanceof Player player) chat(msg,player);
        if(sender instanceof ConsoleCommandSender) info(msg);
        else warn("未知的 Sender 类型："+sender.getClass().getName()+"消息："+msg);
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
                .forEach(p -> notifier.chat(msg, p));
    }

    public static void broadcastChat(List<Player> players, String msg) {
        players.forEach(p -> {
            if (!p.isOnline()) return;
            notifier.chat(msg, p);
        });
    }
    public static void broadcastSound(List<Player> players, Sound sound,float v,float v1) {
        players.forEach(p -> {
            if(!p.isOnline())return;
            p.playSound(p.getLocation(),sound,v,v1);
        });
    }

    /**
     * 以 BossBar 的形式展示3秒
     */
    public static void bossBar(Player player, String msg) {
        BossBar bossBar;
        if (barMap.containsKey(player.getUniqueId())) bossBar = barMap.get(player.getUniqueId());
        else {
            bossBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
            barMap.put(player.getUniqueId(), bossBar);
        }
        bossBar.setTitle(msg);
        bossBar.addPlayer(player);
        if (barTaskMap.containsKey(player.getUniqueId())) {
            int taskId = barTaskMap.get(player.getUniqueId());
            Bukkit.getScheduler().cancelTask(taskId);
        }
        barTaskMap.put(player.getUniqueId(),
                Bukkit.getScheduler().runTaskLater(Siriuxa.getInstance(),
                        () -> bossBar.removePlayer(player), 20 * 3).getTaskId());
    }
}
