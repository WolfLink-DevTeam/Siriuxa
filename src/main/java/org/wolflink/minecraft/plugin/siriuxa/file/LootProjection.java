package org.wolflink.minecraft.plugin.siriuxa.file;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum LootProjection {
    ZOMBIE("entityType.ZOMBIE", "僵尸"),
    SPIDER("EntityType.SPIDER", "蜘蛛"),
    SKELETON("EntityType.SKELETON", "骷髅"),
    CREEPER("EntityType.CREEPER", "爬行者"),
    DROWNED("EntityType.DROWNED", "溺尸"),
    ZOMBIE_VILLAGER("EntityType.ZOMBIE_VILLAGER", "僵尸村民"),
    HUSK("EntityType.HUSK", "尸壳"),
    STRAY("EntityType.STRAY", "流浪者"),
    SILVERFISH("EntityType.SILVERFISH", "蠹虫"),
    VEX("EntityType.VEX", "恼鬼"),
    ENDERMAN("EntityType.ENDERMAN", "末影人"),
    VINDICATOR("EntityType.VINDICATOR", "卫道士"),
    ENDERMITE("EntityType.ENDERMITE", "末影螨"),
    CAVE_SPIDER("EntityType.CAVE_SPIDER", "洞穴蜘蛛"),
    WITCH("EntityType.WITCH", "女巫"),

    ;
    @Getter
    private final String path;
    @Getter
    private final Object defaultValue;

    LootProjection(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public static Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        for (LootProjection lootProjection : LootProjection.values()) {
            map.put(lootProjection.path, lootProjection.defaultValue);
        }
        return map;
    }
}
