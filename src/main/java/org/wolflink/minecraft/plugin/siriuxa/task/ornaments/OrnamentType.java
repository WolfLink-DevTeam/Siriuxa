package org.wolflink.minecraft.plugin.siriuxa.task.ornaments;

public enum OrnamentType {
    /**
     * 幽匿感染
     * 怪物死亡有概率爆发幽匿感染，世界中也会自然爆发幽匿感染
     * 玩家在幽匿方块附近会获得感染值，感染值累计到一定程度玩家会获得负面BUFF
      */
    SCULK_INFECTION,
    /**
     * 安全作业
     * 玩家可以托运物资进入任务
     */
    SAFE_WORKING,
    /**
     * 物资收集
     * 任务中的物资在任务结束后可以带回一部分
     */
    SUPPLIES_COLLECTION,
    /**
     * 潘多拉试炼
     * 任务开始后将由房主选择任务tag，增加任务难度并提高任务报酬，
     * 或者降低任务难度并减少任务报酬。
     */
    PANDORA_TRIAL,
    /**
     * AI强化
     * 类似史诗攻城模组
     */
    SMART_AI,
    /**
     * 塔防
     * 任务中玩家收集的材料可以提交到团队仓库变成通用建材
     * 消耗通用建材可以快速建造一些建筑设施，
     * 如：防御塔，资源产出结构，传送门等。
     */
    TOWER_DEFENSE,
}
