package org.wolflink.minecraft.plugin.siriuxa.task.listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskStat;
import org.wolflink.minecraft.plugin.siriuxa.task.events.WheatBlockSellEvent;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统计监听器
 * 行走距离
 * 击杀怪物数
 * 出售矿物数
 * 造成伤害数
 */
@Singleton
public class StatListener extends WolfirdListener {

    private final Set<TaskStat> availableStats = Collections.synchronizedSet(new HashSet<>());
    public void addStat(TaskStat taskStat) {
        availableStats.add(taskStat);
    }
    public void removeStat(TaskStat taskStat) {
        availableStats.remove(taskStat);
    }

    @Override
    public void onEnable() {
        countDistance();
    }
    @Override
    public void onDisable() {
        getSubScheduler().cancelAllTasks();
        lastLocationMap.clear();
        availableStats.clear();
    }
    Map<UUID, Location> lastLocationMap = new ConcurrentHashMap<>();
    private void countDistance() {
        getSubScheduler().runTaskTimerAsync(()->{
            for (TaskStat taskStat : availableStats){
                for (Player player : taskStat.getTask().getTaskPlayers()) {
                    UUID uuid = player.getUniqueId();
                    Location lastLocation = lastLocationMap.get(uuid);
                    Location nowLocation = player.getLocation();
                    lastLocationMap.put(uuid,nowLocation);
                    if(lastLocation == null || lastLocation.getWorld() == null) continue;
                    // 两个坐标处在同一个世界
                    if(lastLocation.getWorld().equals(nowLocation.getWorld())) {
                        int distance = (int) nowLocation.distance(lastLocation);
                        // 在5秒内移动 100格 以内视为有效
                        if(distance <= 100) {
                            taskStat.getTravelDistanceMap().putIfAbsent(uuid,0);
                            taskStat.getTravelDistanceMap().computeIfPresent(uuid,(ignore,value) -> value+distance);
                        }
                    }
                }
            }
        },20 * 5,20 * 5);
    }
    @Inject
    TaskRepository taskRepository;

    @Nullable
    private TaskStat getTaskStat(Player player) {
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if(task == null) return null;
        return task.getTaskStat();
    }

    // 判断玩家是否对应 TaskStat 记录
    private boolean statNotAvailable(TaskStat taskStat) {
        return !availableStats.contains(taskStat);
    }


    @EventHandler
    void countDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager().getType() != EntityType.PLAYER) return; // 不是玩家
        Player player = (Player) event.getDamager();
        TaskStat taskStat = getTaskStat(player);
        if(taskStat == null) return;
        if(statNotAvailable(taskStat)) return; // 不可被统计
        UUID uuid = player.getUniqueId();
        taskStat.getDamageMap().putIfAbsent(uuid,0);
        taskStat.getDamageMap().computeIfPresent(uuid,(ignore,value)-> (int) (value + event.getFinalDamage()));
    }
    @EventHandler
    void countMobKill(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Monster monster)) return; // 不是怪物
        Player killer = monster.getKiller();
        if(killer == null) return; // 没有杀手
        TaskStat taskStat = getTaskStat(killer);
        if(taskStat == null) return;
        if(statNotAvailable(taskStat)) return;// 不可用统计
        UUID uuid = killer.getUniqueId();
        taskStat.getMobKillMap().putIfAbsent(uuid,0);
        taskStat.getMobKillMap().computeIfPresent(uuid,(ignore,value)-> value+1);
    }
    @EventHandler
    void countOreSell(WheatBlockSellEvent event) {
        Player player = event.getPlayer();
        TaskStat taskStat = getTaskStat(player);
        if(taskStat == null) return;
        if(statNotAvailable(taskStat)) return; // 不可用统计
        UUID uuid = player.getUniqueId();
        taskStat.getOreSellMap().putIfAbsent(uuid,0);
        taskStat.getOreSellMap().computeIfPresent(uuid,(ignore,value)-> value+1);
    }
}
