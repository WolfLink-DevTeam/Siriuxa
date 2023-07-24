package org.wolflink.minecraft.plugin.siriuxa.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Tier {
    /**
     * 普通，优秀，稀有，史诗，传说
     * 分别对应颜色符号
     * §f §a §b §d §6
     */
    COMMON("普通", "§f"),
    UNCOMMON("优秀", "§a"),
    RARE("稀有", "§b"),
    EPIC("史诗", "§d"),
    LEGENDARY("传说", "§6");

    private final String displayName;
    private final String color;
}
