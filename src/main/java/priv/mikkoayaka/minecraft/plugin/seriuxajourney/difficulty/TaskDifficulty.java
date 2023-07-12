package priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty;

/**
 * @param level                     难度等级
 * @param name                      难度名称
 * @param wheatCost                 成本
 * @param wheatLostAcceleratedSpeed 麦穗流速加快倍率(每5分钟)
 * @param hurtWheatCost             受伤麦穗惩罚(每1点伤害)
 * @param hurtDamageMultiple            伤害倍率
 * @param bringSlotAmount           带回格数
 * @param wheatGainPercent          麦穗转化率
 * @param expGainPercent            经验转化率
 */
public record TaskDifficulty(
        int level,
        String name,
        int wheatCost,
        double wheatLostAcceleratedSpeed,
        double hurtWheatCost,
        double hurtDamageMultiple,
        int bringSlotAmount,
        double wheatGainPercent,
        double expGainPercent) {
}
