package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.region;

import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 任务活动区域
 */
public abstract class TaskRegion {


    /**
     * 边界半径
     */
    @Getter
    protected final double radius;
    protected final Task task;
    @Getter
    protected final Location center;
    public TaskRegion(Task task, World world, int centerX, int centerZ, double radius) {
        int highestY = world.getHighestBlockYAt(centerX,centerZ);
        this.center = new Location(world,centerX,highestY,centerZ);
        this.radius = radius;
        this.task = task;
    }

    private int taskId = -1;
    public void startCheck() {
        taskId = Bukkit.getScheduler().runTaskTimer(SeriuxaJourney.getInstance(),this::check,20,20).getTaskId();
    }
    public void stopCheck() {
        if(taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
    }
    /**
     * 检测玩家是否在边界外
     * 是则造成伤害
     */
    private void check() {
        for (Player player : task.getPlayers()) {
            if(isPlayerOutOfBorder(player)) {
                player.damage(2.0);
                player.playSound(player.getLocation(), Sound.ENTITY_SQUID_SQUIRT,1f,1f);
                player.sendTitle("§c§l已离开勘探任务范围","§c外围区域感染严重，请立刻返回！",4,12,4);
            }
            double percent = distanceToBorderPercent(player);
            if(percent <= 0.03) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,20 * 5,0));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§8[ §e! §8] §c被严重感染的空气使你感到不适..."));
            } else {
                int temp = (int)(percent*50);
                String lineColor = "§a";
                if(temp <= 15)lineColor = "§e";
                if(temp <= 5)lineColor = "§c";
                String progressBar = "§f边界 §8| "+lineColor+"§m"+" ".repeat(temp)+"§r§f你§7§m"+" ".repeat(50 - temp)+"§r §8| §f中心";
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(progressBar));
            }
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
        if(task.getPlayers().size() == 0)return null;
        Location averangeLocation = getPlayerAverangeLocation();
        List<Location> availableLocations = new ArrayList<>();
        for (int angle = 0; angle < 360; angle+=30) {
            double radians = Math.toRadians(angle);
            Location temp = averangeLocation.clone();
            temp.add(distance * Math.cos(radians),0,distance * Math.sin(radians));
            if(distanceToBorderPercent(temp) <= 0.05)continue;
            availableLocations.add(temp);
        }
        if(availableLocations.size() == 0) {
            throw new IllegalStateException("撤离点生成时出现问题：没有任何一个可用撤离点");
        }
        return availableLocations.get((int) (Math.random() * availableLocations.size()));
    }

    /**
     * 获取任务中玩家的平均坐标
     */
    public Location getPlayerAverangeLocation() {
        World world = null;
        List<Player> playerList = task.getPlayers();
        double totalX = 0;
        double totalZ = 0;
        for (Player player : playerList) {
            if(world == null) world = player.getWorld();
            totalX += player.getLocation().getX();
            totalZ += player.getLocation().getZ();
        }
        return new Location(world,totalX / playerList.size(),60,totalZ / playerList.size());
    }
}
