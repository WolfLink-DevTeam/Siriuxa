package org.wolflink.minecraft.plugin.siriuxa.difficulty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class LumenTaskDifficulty extends TaskDifficulty {
    /**
     * 麦穗成本
     */
    protected final int lumenCost;
    /**
     * 麦穗补助
     */
    protected final int lumenSupply;
    /**
     * 基础麦穗流失速度
     */
    protected final double baseLumenLoss;
    /**
     * 麦穗流速加快倍率(每5分钟)
     */
    protected final double lumenLostAcceleratedSpeed;
    /**
     * 受伤麦穗惩罚(每1点伤害)
     */
    protected final double hurtLumenCost;
    /**
     * 受到伤害倍率
     */
    protected final double hurtDamageMultiple;
    /**
     * 结算奖励倍率
     */
    protected final double rewardMultiple;
    public LumenTaskDifficulty(Material icon, String color, int level, String name, int lumenCost, int lumenSupply, double baseLumenLoss, double lumenLostAcceleratedSpeed, double hurtLumenCost, double hurtDamageMultiple, double rewardMultiple) {
        super(icon,color,level,name);
        this.lumenCost = lumenCost;
        this.lumenSupply = lumenSupply;
        this.baseLumenLoss = baseLumenLoss;
        this.lumenLostAcceleratedSpeed = lumenLostAcceleratedSpeed;
        this.hurtLumenCost = hurtLumenCost;
        this.hurtDamageMultiple = hurtDamageMultiple;
        this.rewardMultiple = rewardMultiple;
    }
    public LumenTaskDifficulty(Map<String, Object> map) {
        super(map);
        this.lumenCost = (int) map.get("lumenCost");
        this.lumenSupply = (int) map.get("lumenSupply");
        this.baseLumenLoss = (double) map.get("baseLumenLoss");
        this.lumenLostAcceleratedSpeed = (double) map.get("lumenLostAcceleratedSpeed");
        this.hurtLumenCost = (double) map.get("hurtLumenCost");
        this.hurtDamageMultiple = (double) map.get("hurtDamageMultiple");
        this.rewardMultiple = (double) map.get("rewardMultiple");
    }
    @Override
    @NotNull
    public Map<String, Object> serialize() {
        Map<String,Object> map = super.serialize();
        map.put("lumenCost", lumenCost);
        map.put("lumenSupply", lumenSupply);
        map.put("baseLumenLoss", baseLumenLoss);
        map.put("lumenLostAcceleratedSpeed", lumenLostAcceleratedSpeed);
        map.put("hurtLumenCost", hurtLumenCost);
        map.put("hurtDamageMultiple",hurtDamageMultiple);
        map.put("rewardMultiple",rewardMultiple);
        return map;
    }
}