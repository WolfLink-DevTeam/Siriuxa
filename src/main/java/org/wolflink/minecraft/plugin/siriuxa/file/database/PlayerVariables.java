package org.wolflink.minecraft.plugin.siriuxa.file.database;

import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 玩家通用变量数据
 * 存放一些基本变量
 */
@Data
public class PlayerVariables implements ConfigurationSerializable {
    private UUID ownerUuid;
    private int safeSlotAmount = 0;

    public PlayerVariables(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public PlayerVariables(Map<String, Object> map) {
        this.ownerUuid = UUID.fromString((String) map.get("ownerUuid"));
        this.safeSlotAmount = (int) map.get("safeSlotAmount");
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("ownerUuid", ownerUuid.toString());
        map.put("safeSlotAmount", safeSlotAmount);
        return map;
    }

    public static PlayerVariables deserialize(Map<String, Object> map) {
        return new PlayerVariables(map);
    }
}
