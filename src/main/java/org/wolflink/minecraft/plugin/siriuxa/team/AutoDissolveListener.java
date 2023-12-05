package org.wolflink.minecraft.plugin.siriuxa.team;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 队长离线超过3分钟自动解散队伍
 */
@Singleton
public class AutoDissolveListener extends WolfirdListener {
    @Inject
    private GlobalTeamService globalTeamService;
    @Inject
    private GlobalTeamRepository globalTeamRepository;
    private final Map<UUID, Integer> dissolveTaskMap = new ConcurrentHashMap<>();

    @EventHandler
    void on(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        GlobalTeam globalTeam = globalTeamRepository.findByPlayer(event.getPlayer());
        // 处于队伍中，而且玩家是队长，尝试解散队伍
        if (globalTeam != null && globalTeam.getOwnerUuid() == uuid) {
            int taskId = getSubScheduler().runTaskLater(() -> {
                if (dissolveTaskMap.containsKey(uuid)) {
                    dissolveTaskMap.remove(uuid);
                    globalTeamService.dissolve(globalTeam);
                }
            }, 20 * 60 * 3);
            dissolveTaskMap.put(uuid, taskId);
        }

    }

    @EventHandler
    void on(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (dissolveTaskMap.containsKey(uuid)) {
            getSubScheduler().cancelTask(dissolveTaskMap.get(uuid));
            dissolveTaskMap.remove(uuid);
        }
    }
}
