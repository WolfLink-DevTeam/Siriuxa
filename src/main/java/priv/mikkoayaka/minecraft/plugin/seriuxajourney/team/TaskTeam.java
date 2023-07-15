package priv.mikkoayaka.minecraft.plugin.seriuxajourney.team;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskTeam {
    private final Set<UUID> memberUuids = new HashSet<>();
    private final Task task;
    public TaskTeam(Task task) {
        this.task = task;
    }
    public List<Player> getPlayers() {
        return memberUuids.stream()
                .map(Bukkit::getPlayer)
                .filter(p -> p!=null&&p.isOnline())
                .collect(Collectors.toList());
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
    public void join(Player player) {
        join(player.getUniqueId());
    }
    public void join(UUID uuid) {
        memberUuids.add(uuid);
    }
    public void leave(UUID uuid) {
        memberUuids.remove(uuid);
    }
    public void leave(Player player) {
        leave(player.getUniqueId());
    }
}
