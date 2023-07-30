package org.wolflink.minecraft.plugin.siriuxa.file.database;

import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.wolflink.minecraft.plugin.siriuxa.backpack.PlayerBackpack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 离线玩家记录数据
 */
@Data
public class OfflinePlayerRecord implements ConfigurationSerializable {
    private final UUID uuid;
    private final String name;
    private final PlayerBackpack playerBackpack;
    private boolean taskEscape;
    public OfflinePlayerRecord(Player player) {
        uuid = player.getUniqueId();
        name = player.getName();
        playerBackpack = new PlayerBackpack(player);
        taskEscape = false;
    }

    public OfflinePlayerRecord(Map<String, Object> map) {
        uuid = UUID.fromString((String) map.get("uuid"));
        name = (String) map.get("name");
        playerBackpack = (PlayerBackpack) map.get("playerBackpack");
        taskEscape = (boolean) map.get("taskEscape");
    }

    public static OfflinePlayerRecord deserialize(Map<String, Object> map) {
        return new OfflinePlayerRecord(map);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid.toString());
        map.put("name", name);
        map.put("playerBackpack", playerBackpack);
        map.put("taskEscape", taskEscape);
        return map;
    }
}
