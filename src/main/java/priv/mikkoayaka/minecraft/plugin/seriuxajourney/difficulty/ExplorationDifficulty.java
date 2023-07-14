package priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Material;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExplorationDifficulty extends TaskDifficulty{
    /**
     * 麦穗成本
     */
    private final int wheatCost;
    /**
     * 麦穗补助
     */
    private final int wheatSupply;

    /**
     * 受伤麦穗惩罚(每1点伤害)
     */
    private final double hurtWheatCost;
    /**
     * 伤害倍率
     */
    private final double hurtDamageMultiple;

    /**
     * 带回格数
     */
    private final int bringSlotAmount;

    /**
     * 麦穗转化率
     */
    private final double wheatGainPercent;
    /**
     * 经验转化率
     */
    private final double expGainPercent;

    @Builder
    public ExplorationDifficulty(Material icon, String color, int level, String name, int wheatCost, int wheatSupply,double baseWheatLoss, double wheatLostAcceleratedSpeed, double hurtWheatCost, double hurtDamageMultiple, int bringSlotAmount, double wheatGainPercent, double expGainPercent) {
        super(icon, color, level, name,baseWheatLoss,wheatLostAcceleratedSpeed);
        this.wheatCost = wheatCost;
        this.wheatSupply = wheatSupply;
        this.hurtWheatCost = hurtWheatCost;
        this.hurtDamageMultiple = hurtDamageMultiple;
        this.bringSlotAmount = bringSlotAmount;
        this.wheatGainPercent = wheatGainPercent;
        this.expGainPercent = expGainPercent;
    }
}
