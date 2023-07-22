package org.wolflink.minecraft.plugin.siriuxa.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Data
public abstract class Team {
    private final Set<UUID> memberUuids;

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
    void join(Player player) {
        join(player.getUniqueId());
    }

    void join(UUID uuid) { memberUuids.add(uuid); }

    void leave(UUID uuid) {
        memberUuids.remove(uuid);
    }

    public boolean isEmpty() {
        return memberUuids.isEmpty();
    }
    public void clear() {
        memberUuids.clear();
    }

    void leave(Player player) {
        leave(player.getUniqueId());
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
}