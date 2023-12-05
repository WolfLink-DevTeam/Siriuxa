package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.safeworking.OrnamentSafeWorking;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.sculkinfection.OrnamentSculkInfection;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.smartai.OrnamentSmartAI;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.suppliescollection.OrnamentSuppliesCollection;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.timetrap.OrnamentTimeTrap;

public enum OrnamentType {
    /**
     * 幽匿感染
     * 怪物死亡有概率爆发幽匿感染，世界中也会自然爆发幽匿感染
     * 玩家在幽匿方块附近会获得感染值，感染值累计到一定程度玩家会获得负面BUFF
      */
    SCULK_INFECTION(OrnamentSculkInfection.class),
    /**
     * 安全作业
     * 玩家可以托运物资进入任务
     */
    SAFE_WORKING(OrnamentSafeWorking.class),
    /**
     * 物资收集
     * 任务中的物资在任务结束后可以带回一部分
     */
    SUPPLIES_COLLECTION(OrnamentSuppliesCollection.class),
    /**
     * 潘多拉试炼
     * 任务开始后将由房主选择任务tag，增加任务难度并提高任务报酬，
     * 或者降低任务难度并减少任务报酬。
     */
    PANDORA_TRIAL(null),
    /**
     * AI强化
     * 类似史诗攻城模组
     */
    SMART_AI(OrnamentSmartAI.class),
    /**
     * 时间陷阱
     * 在该类型的任务中幽匿光体会逐渐流失，
     * 玩家需要不断收集幽匿光体，可以通过杀怪、挖矿、采集等方式获得
     * 玩家受伤也会扣除幽匿光体
     */
    TIME_TRAP(OrnamentTimeTrap.class)
    ;
//    /**
//     * 塔防
//     * 任务中玩家收集的材料可以提交到团队仓库变成通用建材
//     * 消耗通用建材可以快速建造一些建筑设施，
//     * 如：防御塔，资源产出结构，传送门等。
//     */
//    TOWER_DEFENSE
    @Getter
    private final Class<? extends TaskOrnament> ornamentClass;
    OrnamentType(Class<? extends TaskOrnament> ornamentClass) {
        this.ornamentClass = ornamentClass;
    }
    @Nullable
    public <T extends TaskOrnament> T getTaskOrnament() {
        return (T) IOC.getBean(this.ornamentClass);
    }
}
