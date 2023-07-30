package org.wolflink.minecraft.plugin.siriuxa.task.regions;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 任务活动区域
 */
public abstract class TaskArea {


    /**
     * 边界半径
     */
    @Getter
    protected final double radius;
    protected final Task task;
    @Getter
    protected final Location center;

    private final Random random = new Random();
    private int taskId = -1;

    protected TaskArea(Task task, Location center, double radius) {
        this.center = center;
        this.radius = radius;
        this.task = task;
    }

    public void startCheck() {
        taskId = Bukkit.getScheduler().runTaskTimer(Siriuxa.getInstance(), this::check, 20, 20).getTaskId();
    }

    public void stopCheck() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    /**
     * 获取边界进度条
     */
    private String getBorderBar(Player player) {
        double percent = distanceToBorderPercent(player);
        int temp = (int) Math.round(percent * 50);
        if(temp < 0) temp = 0;
        String lineColor = "§a";
        if (temp <= 15) lineColor = "§e";
        if (temp <= 5) lineColor = "§c";
        String progressBar = "§f%img_mc_barrier% " + lineColor + "§m" + " ".repeat(temp) + "§r§f%img_mc_totem_of_undying%§7§m" + " ".repeat(50 - temp) + "§r §f%img_mc_end_crystal%";
        return PlaceholderAPI.setPlaceholders(player, progressBar);
    }

    /**
     * 检测玩家是否在边界外
     * 是则造成伤害
     */
    private void check() {
        for (Player player : task.getTaskPlayers()) {
            if (isPlayerOutOfBorder(player)) {
                player.damage(2.0);
                player.playSound(player.getLocation(), Sound.ENTITY_SQUID_SQUIRT, 1f, 1f);
                player.sendTitle("§c§l已离开勘探任务范围", "§c外围区域感染严重，请立刻返回！", 4, 12, 4);
            }
            double percent = distanceToBorderPercent(player);
            if (percent <= 0.03) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 0));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8[ §e! §8] §c被严重感染的空气使你感到不适..."));
            }
            Notifier.bossBar(player, getBorderBar(player));
        }
    }


    /**
     * 当前坐标距离最短边界距离 / 边界半径
     */
    public double distanceToBorderPercent(Player player) {
        return distanceToBorderPercent(player.getLocation());
    }

    public double distanceToBorderPercent(Location location) {
        double distance = distanceToNearestBorder(location);
        return distance / radius;
    }

    /**
     * 距离最近的边界有多远(可以为负数，意味着玩家已离开边界)
     */
    public abstract double distanceToNearestBorder(Location location);

    public double distanceToNearestBorder(Player player) {
        return distanceToNearestBorder(player.getLocation());
    }

    /**
     * 玩家是否离开了活动区域
     */
    public boolean isPlayerOutOfBorder(Player player) {
        return distanceToNearestBorder(player) <= 0;
    }

    /**
     * 获得合理的撤离点坐标
     * 尽量离所有玩家都远
     */
    public Location getEvacuateLocation(int distance) {
        if (task.getTaskPlayers().isEmpty()) return null;
        Location averangeLocation = getPlayerAverangeLocation();
        List<Location> availableLocations = new ArrayList<>();
        for (int angle = 0; angle < 360; angle += 30) {
            double radians = Math.toRadians(angle);
            Location temp = averangeLocation.clone();
            temp.add(distance * Math.cos(radians), 0, distance * Math.sin(radians));
            if (distanceToBorderPercent(temp) <= 0.05) continue;
            availableLocations.add(temp);
        }
        if (availableLocations.isEmpty()) {
            throw new IllegalStateException("撤离点生成时出现问题：没有任何一个可用撤离点");
        }
        return availableLocations.get(random.nextInt(availableLocations.size()));
    }

    /**
     * 获取任务中玩家的平均坐标
     */
    public Location getPlayerAverangeLocation() {
        World world = null;
        List<Player> playerList = task.getTaskPlayers();
        double totalX = 0;
        double totalZ = 0;
        for (Player player : playerList) {
            if (world == null) world = player.getWorld();
            totalX += player.getLocation().getX();
            totalZ += player.getLocation().getZ();
        }
        return new Location(world, totalX / playerList.size(), 60, totalZ / playerList.size());
    }
}
