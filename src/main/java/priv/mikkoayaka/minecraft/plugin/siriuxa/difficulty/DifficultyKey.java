package priv.mikkoayaka.minecraft.plugin.siriuxa.difficulty;

/**
 * 难度主键
 *
 * @param clazz 难度类
 * @param level 难度等级
 */
public record DifficultyKey(Class<?> clazz, int level) {
}
