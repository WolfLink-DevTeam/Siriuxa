package org.wolflink.minecraft.plugin.siriuxa.loot;

import lombok.Getter;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;

import java.util.Arrays;

public enum LootRarity {
    MISFORTUNE("§0不幸",50,-5), // 5%
    COMMON("§a常见",500,0), // 50%
    RARE("§b稀有",400,5), // 40%
    EPIC("§d§l史诗",35,10), // 3.5%
    LEGENDARY("§6§l传说",15,15); // 1.5%

    @Getter
    private final String displayName;
    @Getter
    private final int weight;
    @Getter
    private final int level;
    LootRarity(String displayName,int weight,int level) {
        this.displayName = displayName;
        this.weight = weight;
        this.level = level;
    }
    public static LootRarity getRandom() {
        int total = Arrays.stream(values()).map(LootRarity::getWeight).reduce(0,Integer::sum);
        int rand = (int) (Math.random() * total);
        for (LootRarity lootRarity : values()) {
            if(rand < lootRarity.weight) return lootRarity;
            rand -= lootRarity.weight;
        }
        Notifier.error("获取随机品质时出现问题。");
        return COMMON;
    }
}
