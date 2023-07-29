package org.wolflink.minecraft.plugin.siriuxa.difficulty;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.wolflink.minecraft.plugin.siriuxa.api.INameable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class WheatTaskDifficulty extends TaskDifficulty {
    /**
     * 麦穗成本
     */
    protected final int wheatCost;
    /**
     * 麦穗补助
     */
    protected final int wheatSupply;
    /**
     * 基础麦穗流失速度
     */
    protected final double baseWheatLoss;
    /**
     * 麦穗流速加快倍率(每5分钟)
     */
    protected final double wheatLostAcceleratedSpeed;
    /**
     * 受伤麦穗惩罚(每1点伤害)
     */
    protected final double hurtWheatCost;
    /**
     * 受到伤害倍率
     */
    protected final double hurtDamageMultiple;
    /**
     * 结算奖励倍率
     */
    protected final double rewardMultiple;
    public WheatTaskDifficulty(Material icon, String color, int level, String name, int wheatCost, int wheatSupply, double baseWheatLoss, double wheatLostAcceleratedSpeed, double hurtWheatCost, double hurtDamageMultiple, double rewardMultiple) {
        super(icon,color,level,name);
        this.wheatCost = wheatCost;
        this.wheatSupply = wheatSupply;
        this.baseWheatLoss = baseWheatLoss;
        this.wheatLostAcceleratedSpeed = wheatLostAcceleratedSpeed;
        this.hurtWheatCost = hurtWheatCost;
        this.hurtDamageMultiple = hurtDamageMultiple;
        this.rewardMultiple = rewardMultiple;
    }
    public WheatTaskDifficulty(Map<String, Object> map) {
        super(map);
        this.wheatCost = (int) map.get("wheatCost");
        this.wheatSupply = (int) map.get("wheatSupply");
        this.baseWheatLoss = (double) map.get("baseWheatLoss");
        this.wheatLostAcceleratedSpeed = (double) map.get("wheatLostAcceleratedSpeed");
        this.hurtWheatCost = (double) map.get("hurtWheatCost");
        this.hurtDamageMultiple = (double) map.get("hurtDamageMultiple");
        this.rewardMultiple = (double) map.get("rewardMultiple");
    }
    @Override
    @NotNull
    public Map<String, Object> serialize() {
        Map<String,Object> map = super.serialize();
        map.put("wheatCost",wheatCost);
        map.put("wheatSupply",wheatSupply);
        map.put("baseWheatLoss",baseWheatLoss);
        map.put("wheatLostAcceleratedSpeed",wheatLostAcceleratedSpeed);
        map.put("hurtWheatCost",hurtWheatCost);
        map.put("hurtDamageMultiple",hurtDamageMultiple);
        map.put("rewardMultiple",rewardMultiple);
        return map;
    }
}