package org.wolflink.minecraft.plugin.siriuxa.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Data
public abstract class Team {

    private final UUID ownerUuid;
    private final Set<UUID> memberUuids;

    public OfflinePlayer getOfflineOwner() {
        return Bukkit.getOfflinePlayer(ownerUuid);
    }
    @Nullable
    public OfflinePlayer getOfflinePlayer(String name) {
        for (OfflinePlayer offlinePlayer : getOfflinePlayers()) {
            if(offlinePlayer.getName() == null) continue;
            if(offlinePlayer.getName().equalsIgnoreCase(name)) return offlinePlayer;
        }
        return null;
    }
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

    void join(UUID uuid) {
        memberUuids.add(uuid);
    }

    void leave(UUID uuid) {
        memberUuids.remove(uuid);
    }

    public boolean isEmpty() {
        return memberUuids.isEmpty();
    }

    public void clear() {
        memberUuids.clear();
    }

    void leave(OfflinePlayer offlinePlayer) { leave(offlinePlayer.getUniqueId()); }
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
