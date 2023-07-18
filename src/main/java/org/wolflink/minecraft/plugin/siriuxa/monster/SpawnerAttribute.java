package org.wolflink.minecraft.plugin.siriuxa.monster;

import lombok.Getter;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

/**
 * 怪物生成器的属性类
 */
@Getter
public class SpawnerAttribute {
    private final double healthMultiple;
    private final double movementMultiple;
    private final double damageMultiple;
    public SpawnerAttribute(TaskDifficulty taskDifficulty) {
        switch (taskDifficulty.getLevel()) {
            case 1 -> { // 轻松
                healthMultiple = 0.6;
                movementMultiple = 0.8;
                damageMultiple = 0.6;
            }
            case 2 -> { // 常规
                healthMultiple = 1.0;
                movementMultiple = 1.0;
                damageMultiple = 1.0;
            }
            case 3 -> { // 困难
                healthMultiple = 1.5;
                movementMultiple = 1.2;
                damageMultiple = 1.5;
            }
            case 4 -> { // 专家
                healthMultiple = 2.0;
                movementMultiple = 1.5;
                damageMultiple = 2.0;
            }
            default -> {
                healthMultiple = 1.0;
                movementMultiple = 1.0;
                damageMultiple = 1.0;
                Notifier.error("不支持的难度等级："+taskDifficulty.getLevel());
            }
        }
    }
}
