package org.wolflink.minecraft.plugin.siriuxa.difficulty;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ExplorationDifficulty extends WheatTaskDifficulty implements ConfigurationSerializable {
    /**
     * 带回格数
     */
    private final int bringSlotAmount;
    @Builder
    public ExplorationDifficulty(Material icon, String color, int level, String name, int wheatCost, int wheatSupply, double baseWheatLoss, double wheatLostAcceleratedSpeed, double hurtWheatCost, double hurtDamageMultiple, double rewardMultiple, int bringSlotAmount) {
        super(icon, color, level, name, wheatCost, wheatSupply, baseWheatLoss, wheatLostAcceleratedSpeed, hurtWheatCost, hurtDamageMultiple, rewardMultiple);
        this.bringSlotAmount = bringSlotAmount;
    }
    protected ExplorationDifficulty(Map<String,Object> map) {
        super(map);
        this.bringSlotAmount = (int) map.get("bringSlotAmount");
    }
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = super.serialize();
        map.put("bringSlotAmount",bringSlotAmount);
        return map;
    }
    public static ExplorationDifficulty deserialize(Map<String,Object> map) {
        return new ExplorationDifficulty(map);
    }
}
