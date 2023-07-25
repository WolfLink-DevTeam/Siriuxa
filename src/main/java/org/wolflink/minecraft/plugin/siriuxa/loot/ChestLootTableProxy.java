package org.wolflink.minecraft.plugin.siriuxa.loot;

import lombok.NonNull;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;

import java.util.EnumMap;
import java.util.List;
import java.util.Random;

@Singleton
public class ChestLootTableProxy {

    private static final Random random = new Random();

    private static final EnumMap<LootRarity, List<LootTables>> lootMap = new EnumMap<>(LootRarity.class);

    static {
        // 垃圾
        lootMap.put(LootRarity.MISFORTUNE, List.of(
                LootTables.EMPTY,
                LootTables.JUNGLE_TEMPLE_DISPENSER,
                LootTables.VILLAGE_CARTOGRAPHER,
                LootTables.VILLAGE_DESERT_HOUSE,
                LootTables.VILLAGE_FISHER,
                LootTables.VILLAGE_FLETCHER,
                LootTables.VILLAGE_MASON,
                LootTables.VILLAGE_SAVANNA_HOUSE,
                LootTables.VILLAGE_SHEPHERD,
                LootTables.VILLAGE_SNOWY_HOUSE,
                LootTables.VILLAGE_TANNERY
        ));
        // 普通
        lootMap.put(LootRarity.COMMON, List.of(
                LootTables.VILLAGE_TOOLSMITH,
                LootTables.VILLAGE_TEMPLE,
                LootTables.VILLAGE_TAIGA_HOUSE,
                LootTables.VILLAGE_BUTCHER,
                LootTables.ABANDONED_MINESHAFT,
                LootTables.JUNGLE_TEMPLE,
                LootTables.NETHER_BRIDGE,
                LootTables.BASTION_BRIDGE,
                LootTables.ANCIENT_CITY_ICE_BOX,
                LootTables.SHIPWRECK_SUPPLY,
                LootTables.SPAWN_BONUS_CHEST,
                LootTables.STRONGHOLD_CROSSING,
                LootTables.VILLAGE_PLAINS_HOUSE
        ));
        // 稀有
        lootMap.put(LootRarity.RARE, List.of(
                LootTables.WOODLAND_MANSION,
                LootTables.VILLAGE_WEAPONSMITH,
                LootTables.STRONGHOLD_CORRIDOR,
                LootTables.DESERT_PYRAMID,
                LootTables.IGLOO_CHEST,
                LootTables.PILLAGER_OUTPOST,
                LootTables.BASTION_OTHER,
                LootTables.RUINED_PORTAL,
                LootTables.BASTION_HOGLIN_STABLE,
                LootTables.UNDERWATER_RUIN_BIG,
                LootTables.UNDERWATER_RUIN_SMALL
        ));
        // 史诗 有较好的装备物资
        lootMap.put(LootRarity.EPIC, List.of(
                LootTables.SHIPWRECK_TREASURE,
                LootTables.BURIED_TREASURE,
                LootTables.SIMPLE_DUNGEON,
                LootTables.STRONGHOLD_LIBRARY
        ));
        // 传说 有钻石物品和丰富物资
        lootMap.put(LootRarity.LEGENDARY, List.of(
                LootTables.END_CITY_TREASURE,
                LootTables.BASTION_TREASURE,
                LootTables.ANCIENT_CITY
        ));
    }


    @NonNull
    public LootTable randomLootTable(LootRarity lootRarity) {
        List<LootTables> lootTablesList = lootMap.get(lootRarity);
        if (lootTablesList == null) {
            Notifier.error("不支持的战利品稀有度：" + lootRarity.name());
            return LootTables.EMPTY.getLootTable();
        }
        return lootTablesList.get(random.nextInt(lootTablesList.size())).getLootTable();
    }
}
