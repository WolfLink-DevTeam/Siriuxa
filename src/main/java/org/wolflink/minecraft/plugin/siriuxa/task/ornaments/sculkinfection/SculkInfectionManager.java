package org.wolflink.minecraft.plugin.siriuxa.task.ornaments.sculkinfection;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.IStatus;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.world.BlockAPI;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskEndEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskStartEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.ornaments.OrnamentType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class SculkInfectionManager extends WolfirdListener implements IStatus {
    @Inject
    private TaskRepository taskRepository;
    private final Set<Material> sculkTypes = Stream.of(Material.SCULK, Material.SCULK_CATALYST).collect(Collectors.toSet());
    private final Set<Task> availableTasks = new HashSet<>();

    /**
     * 感染值
     */
    private final Map<UUID, Integer> infectionMap = new ConcurrentHashMap<>();

    private final SubScheduler subScheduler = new SubScheduler();
    private final Set<UUID> milkCDSet = new HashSet<>();
    @Inject
    private BlockAPI blockAPI;
    @Inject
    private Config config;

    /**
     * 增加感染值
     */
    private void addInfectionValue(Player player, int value) {
        UUID pUuid = player.getUniqueId();
        // 喝了牛奶不再增加感染值
        if (value > 0 && milkPlayers.contains(pUuid)) return;
        int oldValue = getInfectionValue(player.getUniqueId());
        int newValue = oldValue + value;
        if (newValue < 0) newValue = 0;
        if (newValue >= 1200) newValue = 1200;
        infectionMap.put(pUuid, newValue);
    }

    public int getInfectionValue(UUID uuid) {
        return infectionMap.getOrDefault(uuid, 0);
    }

    /**
     * 刷新玩家的感染值
     * 玩家站在潜声方块上，每秒获得 20 点感染值
     * 每秒获得 附近8格内潜声方块数量x1.25 - 20 点感染值，，最多检测64个方块
     * 如果不处在附近，则每秒 -20 点感染值
     * 牛奶可以减少 500 点感染值
     * <p>
     * 轻度感染 达到 300 点 间歇性虚弱+间歇性挖掘疲劳+走过的方块有概率变成潜声方块
     * 中度感染 达到 600 点 虚弱+挖掘疲劳+缓慢+走过的方块有概率变成潜声方块
     * 重度感染 达到 1000 点 虚弱+挖掘疲劳+走过的方块有概率变成潜声方块+凋零+缓慢+失明
     */
    private void updateInfectionValue(Player player) {
        UUID pUuid = player.getUniqueId();
        List<Location> nearbySculks = blockAPI.searchBlock(Material.SCULK, player.getLocation(), 7);
        int sculkAmount = (int) (nearbySculks.size() * 1.25);
        if (sculkAmount >= 48) sculkAmount = 48;
        addInfectionValue(player, sculkAmount - 20);
        Material blockType = player.getLocation().add(0, -1, 0).getBlock().getType();
        if (sculkTypes.contains(blockType)) addInfectionValue(player, 20);
        int value = getInfectionValue(pUuid);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double randDouble = random.nextDouble();
        subScheduler.runTask(() -> {
            if (IOC.getBean(TaskRepository.class).findByTaskTeamPlayer(player) == null) return; // 玩家已经不在任务
            if (value >= 1000) {
                player.playSound(player.getLocation(), Sound.BLOCK_SCULK_CHARGE, 1f, 1f);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§l你被幽匿方块严重感染了！"));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 0, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 0, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 0, false, false, false));
                Material material;
                if (random.nextDouble() <= 0.2) material = Material.SCULK_CATALYST;
                else material = Material.SCULK;
                player.getLocation().clone().add(0, -1, 0).getBlock().setType(material);
            } else if (value >= 600) {
                player.playSound(player.getLocation(), Sound.BLOCK_SCULK_CHARGE, 1f, 1f);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5§l你变得寸步难行..."));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 0, false, false, false));
                if (randDouble <= 0.6) {
                    Material material;
                    if (random.nextDouble() <= 0.15) material = Material.SCULK_CATALYST;
                    else material = Material.SCULK;
                    Location location = player.getLocation().clone().add(0, -1, 0);
                    if (location.getBlock().getType().isSolid()) location.getBlock().setType(material);
                }
            } else if (value >= 300) {
                player.playSound(player.getLocation(), Sound.BLOCK_SCULK_CHARGE, 1f, 1f);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§e§l你感到有些不适..."));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 10, 0, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10, 0, false, false, false));
                if (randDouble <= 0.3) {
                    Material material;
                    if (random.nextDouble() <= 0.1) material = Material.SCULK_CATALYST;
                    else material = Material.SCULK;
                    player.getLocation().clone().add(0, -1, 0).getBlock().setType(material);
                }
            }
        });
    }

    private final Set<UUID> milkPlayers = new HashSet<>();

    @Override
    public void enable() {
        setEnabled(true);
        subScheduler.runTaskTimerAsync(() ->
                        availableTasks.stream()
                                .flatMap(task -> task.getTaskPlayers().stream())
                                .forEach(this::updateInfectionValue),
                20, 20);
    }

    @Override
    public void disable() {
        milkCDSet.clear();
        setEnabled(false);
        subScheduler.cancelAllTasks();
    }

    @EventHandler
    void on(TaskStartEvent event) {
        if (event.getTask().getOrnamentTypes().contains(OrnamentType.SCULK_INFECTION)) {
            availableTasks.add(event.getTask());
        }
    }

    @EventHandler
    void on(TaskEndEvent event) {
        if (event.getTask().getOrnamentTypes().contains(OrnamentType.SCULK_INFECTION)) {
            availableTasks.remove(event.getTask());
        }
    }

    @EventHandler
    void breakSculk(BlockBreakEvent event) {
        if (!sculkTypes.contains(event.getBlock().getType())) return;
        Task task = taskRepository.findByTaskTeamPlayer(event.getPlayer());
        if (task == null || !availableTasks.contains(task)) return;
        addInfectionValue(event.getPlayer(), 10);
    }

    @EventHandler
    void drinkMilk(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task == null || !availableTasks.contains(task)) return;
        if (item.getType() != Material.MILK_BUCKET) return;
        // 玩家喝了牛奶
        if (milkCDSet.contains(player.getUniqueId())) return;
        if (getInfectionValue(player.getUniqueId()) >= 300) {
            milkCDSet.add(player.getUniqueId());
            milkPlayers.add(player.getUniqueId());
            player.setCooldown(Material.MILK_BUCKET,20 * 480);
            // 5分钟有效期，期间感染值不会增高
            subScheduler.runTaskLater(() -> {
                milkPlayers.remove(player.getUniqueId());
                Notifier.chat("牛奶的效果减退了。",player);
            }, 20 * 300L);
            // 8分钟冷却
            subScheduler.runTaskLater(() -> {
                milkCDSet.remove(player.getUniqueId());
                if (player.isOnline()) Notifier.chat("你可以再次饮用牛奶了。", player);
            }, 20 * 480L);
            addInfectionValue(player, -500);
            Notifier.chat("喝了牛奶之后你感觉好多了。", player);
        }
    }
}