package priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty;

/**
 * @param level             难度等级
 * @param name              难度名称
 * @param wheatCost         成本
 * @param wheatLostMultiple 麦穗流失倍率
 * @param damageMultiple    伤害倍率
 * @param bringSlotAmount   带回格数
 * @param wheatGainPercent  麦穗转化率
 * @param expGainPercent    经验转化率
 */
public record TaskDifficulty(
        int level,
        String name,
        int wheatCost,
        double wheatLostMultiple,
        double damageMultiple,
        int bringSlotAmount,
        double wheatGainPercent,
        double expGainPercent) {
}
