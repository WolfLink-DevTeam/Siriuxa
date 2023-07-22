package org.wolflink.minecraft.plugin.siriuxa.team;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Data
public class Team {
    private UUID teamUuid = UUID.randomUUID();
    private Set<UUID> memberUuids = new HashSet<>();
    /**
     * 当前选择的任务
     */
    @Nullable private Task selectedTask = null;

    public List<Player> getPlayers() {
        return memberUuids.stream()
                .map(Bukkit::getPlayer)
                .filter(p -> p != null && p.isOnline())
                .toList();
    }

    public List<OfflinePlayer> getOfflinePlayers() {
        return memberUuids.stream()
                .map(Bukkit::getOfflinePlayer)
                .toList();
    }

    public int size() {
        return memberUuids.size();
    }

    public boolean contains(UUID uuid) {
        return memberUuids.contains(uuid);
    }

    public boolean contains(Player player) {
        return contains(player.getUniqueId());
    }

    /**
     * 加入该队伍
     */
    void join(Player player) {
        join(player.getUniqueId());
    }

    void join(UUID uuid) {
        memberUuids.add(uuid);
    }

    void leave(UUID uuid) {
        memberUuids.remove(uuid);
    }

    void clear() {
        for (UUID uuid : memberUuids) {
            leave(uuid);
        }
    }

    void leave(Player player) {
        leave(player.getUniqueId());
    }
}
