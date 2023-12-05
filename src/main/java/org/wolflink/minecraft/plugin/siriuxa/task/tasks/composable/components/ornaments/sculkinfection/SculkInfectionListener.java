package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.sculkinfection;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.bukkit.wolfblockspread.SpreadType;
import org.wolflink.minecraft.bukkit.wolfblockspread.WolfBlockSpreadAPI;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.MetadataKey;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.RandomAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.world.LocationAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.world.SculkSpawnBox;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskEndEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskStartEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ComponentChecker;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.OrnamentType;
import org.wolflink.minecraft.plugin.siriuxa.task.regions.TaskArea;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
class SculkInfectionListener extends WolfirdListener {
    OrnamentSculkInfection manager;
    final Set<UUID> milkPlayers = new HashSet<>();
    private final Set<UUID> milkCDSet = new HashSet<>();

    @EventHandler
    void taskStart(TaskStartEvent event) {
        if (ComponentChecker.taskHasComponent(event.getTask(),OrnamentType.SCULK_INFECTION)) {
            manager.availableTasks.add(event.getTask());
            TaskArea taskArea = event.getTask().getTaskRegion().getTaskArea();
            if (taskArea != null) {
                manager.availableWorlds.add(Objects.requireNonNull(taskArea.getCenter().getWorld()).getName());
            }
        }
    }

    @EventHandler
    void taskEnd(TaskEndEvent event) {
        if (ComponentChecker.taskHasComponent(event.getTask(),OrnamentType.SCULK_INFECTION)) {
            manager.availableTasks.remove(event.getTask());
        }
    }

    @EventHandler
    void breakSculk(BlockBreakEvent event) {
        if (!manager.sculkTypes.contains(event.getBlock().getType())) return;
        Task task = manager.taskRepository.findByTaskTeamPlayer(event.getPlayer());
        if (task == null || !manager.availableTasks.contains(task)) return;
        manager.addInfectionValue(event.getPlayer(), 10);
    }

    @EventHandler
    void drinkMilk(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        Task task = manager.taskRepository.findByTaskTeamPlayer(player);
        if (task == null || !manager.availableTasks.contains(task)) return;
        if (item.getType() != Material.MILK_BUCKET) return;
        // 玩家喝了牛奶
        if (milkCDSet.contains(player.getUniqueId())) return;
        if (manager.getInfectionValue(player.getUniqueId()) >= 300) {
            milkCDSet.add(player.getUniqueId());
            milkPlayers.add(player.getUniqueId());
            player.setCooldown(Material.MILK_BUCKET, 20 * 480);
            // 5分钟有效期，期间感染值不会增高
            getSubScheduler().runTaskLater(() -> {
                milkPlayers.remove(player.getUniqueId());
                Notifier.chat("牛奶的效果减退了。", player);
            }, 20 * 300L);
            // 8分钟冷却
            getSubScheduler().runTaskLater(() -> {
                milkCDSet.remove(player.getUniqueId());
                if (player.isOnline()) Notifier.chat("你可以再次饮用牛奶了。", player);
            }, 20 * 480L);
            manager.addInfectionValue(player, -500);
            Notifier.chat("喝了牛奶之后你感觉好多了。", player);
        }
    }

    private static final int SPREAD_BLUEPRINT_ID = WolfBlockSpreadAPI.create(Material.SCULK, SpreadType.SINGLE_SPREAD, 20, 40);

    @EventHandler
    void sculkSpread(EntityDeathEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {
            List<MetadataValue> metadataValueList = event.getEntity().getMetadata(MetadataKey.MONSTER_BELONG_TASK_UUID.getKey());
            if (metadataValueList.isEmpty()) return;
            Task task = IOC.getBean(TaskRepository.class).find((UUID) metadataValueList.get(0).value());
            if (!ComponentChecker.taskHasComponent(task, OrnamentType.SCULK_INFECTION)) return;
            ThreadLocalRandom random = ThreadLocalRandom.current();
            double rand = random.nextDouble();
            // 15%几率生成地基
            if (rand <= 0.15) {
                SculkSpawnBox sculkSpawnBox = new SculkSpawnBox(event.getEntity().getLocation().clone());
                if (sculkSpawnBox.isAvailable()) {
                    Bukkit.getScheduler().runTask(Siriuxa.getInstance(), sculkSpawnBox::spawn);
                }
            }
        });
    }

    private final Random r = new Random();

    @Override
    public void onEnable() {
        getSubScheduler().runTaskTimerAsync(() -> {
            int secs = r.nextInt(180);
            for (Task task : manager.availableTasks) {
                getSubScheduler().runTaskLater(() -> autoSculkSpread(task), 20 * secs);
            }
        }, 20 * 60 * 4, 20 * 60 * 6);
    }

    private void autoSculkSpread(Task task) {
        getSubScheduler().runTaskAsync(() -> {
            Player player = IOC.getBean(RandomAPI.class).selectRandom(task.getTaskPlayers());
            if (player == null || !player.isOnline()) return;
            LocationAPI locationAPI = IOC.getBean(LocationAPI.class);
            for (int i = 0; i < 3; i++) {
                Location solidLoc = locationAPI.getLocationByAngle(player.getLocation(), r.nextInt(360) - 180, 10);
                if (!solidLoc.getBlock().getType().isSolid()) {
                    solidLoc = locationAPI.getNearestSolid(solidLoc, 7);
                }
                if (solidLoc == null) continue;
                Location finalSolidLoc = solidLoc;
                Bukkit.getScheduler().runTask(Siriuxa.getInstance(),
                        () -> WolfBlockSpreadAPI.start(SPREAD_BLUEPRINT_ID, finalSolidLoc));
                break;
            }
        });
    }
}
