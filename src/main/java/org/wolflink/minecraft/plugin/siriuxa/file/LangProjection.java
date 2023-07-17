package org.wolflink.minecraft.plugin.siriuxa.file;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum LangProjection {
    COPPER_BLOCK("material.copper_block", "铜块"),
    COAL_BLOCK("material.coal_block", "煤炭块"),
    IRON_BLOCK("material.iron_block", "铁块"),
    GOLD_BLOCK("material.gold_block", "金块"),
    LAPIS_BLOCK("material.lapis_block", "青金石块"),
    REDSTONE_BLOCK("material.redstone_block", "红石块"),
    DIAMOND_BLOCK("material.diamond_block", "钻石块"),
    EMERALD_BLOCK("material.emerald_block", "绿宝石块"),


    ;
    @Getter
    private final String path;
    @Getter
    private final Object defaultValue;

    LangProjection(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public static Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        for (LangProjection langProjection : LangProjection.values()) {
            map.put(langProjection.path, langProjection.defaultValue);
        }
        return map;
    }
}
