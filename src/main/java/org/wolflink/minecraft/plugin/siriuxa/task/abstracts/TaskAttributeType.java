package org.wolflink.minecraft.plugin.siriuxa.task.abstracts;

public enum TaskAttributeType {
    WHEAT_COST, // 任务花费麦穗
    LUMEN_SUPPLY, // 初始光体补偿
    BASE_LUMEN_LOSS, // 基础光体每秒流速
    LUMEN_LOST_ACC_SPEED, // 光体流失加速度(原为5分钟结算一次，现为1秒结算一次)
    LUMEN_LOST_MULTIPLE_NOW, // 光体实时流失倍率
    HURT_LUMEN_COST, // 受伤光体惩罚
    HURT_DAMAGE_MULTIPLE, // 受伤倍率
    REWARD_MULTIPLE, // 任务报酬倍率
    BRING_SLOT_AMOUNT, // 可带回物品数量
    LUMEN_LEFT, // 剩余幽匿光体数量
    LUMEN_LEFT_LAST_SECOND, // 上一秒剩余光体数量
    LUMEN_LEFT_DELTA, // 光体减少在一秒内的变化数

}
