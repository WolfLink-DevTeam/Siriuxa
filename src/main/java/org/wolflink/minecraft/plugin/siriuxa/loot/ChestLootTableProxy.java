package org.wolflink.minecraft.plugin.siriuxa.loot;

import lombok.NonNull;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Singleton
public class ChestLootTableProxy {

    private final EnumMap<LootRarity, List<LootTables>> lootMap = new EnumMap<>(LootRarity.class) {{
        // 垃圾
        put(LootRarity.MISFORTUNE, new ArrayList<>() {{
            add(LootTables.EMPTY);
            add(LootTables.JUNGLE_TEMPLE_DISPENSER);
            add(LootTables.VILLAGE_CARTOGRAPHER);
            add(LootTables.VILLAGE_DESERT_HOUSE);
            add(LootTables.VILLAGE_FISHER);
            add(LootTables.VILLAGE_FLETCHER);
            add(LootTables.VILLAGE_MASON);
            add(LootTables.VILLAGE_SAVANNA_HOUSE);
            add(LootTables.VILLAGE_SHEPHERD);
            add(LootTables.VILLAGE_SNOWY_HOUSE);
            add(LootTables.VILLAGE_TANNERY);
        }});
        // 普通
        put(LootRarity.COMMON, new ArrayList<>() {{
            add(LootTables.VILLAGE_TOOLSMITH);
            add(LootTables.VILLAGE_TEMPLE);
            add(LootTables.VILLAGE_TAIGA_HOUSE);
            add(LootTables.VILLAGE_BUTCHER);
            add(LootTables.ABANDONED_MINESHAFT);
            add(LootTables.JUNGLE_TEMPLE);
            add(LootTables.NETHER_BRIDGE);
            add(LootTables.BASTION_BRIDGE);
            add(LootTables.ANCIENT_CITY_ICE_BOX);
            add(LootTables.SHIPWRECK_SUPPLY);
            add(LootTables.SPAWN_BONUS_CHEST);
            add(LootTables.STRONGHOLD_CROSSING);
            add(LootTables.VILLAGE_PLAINS_HOUSE);
        }});
        // 稀有
        put(LootRarity.RARE, new ArrayList<>() {{
            add(LootTables.WOODLAND_MANSION);
            add(LootTables.VILLAGE_WEAPONSMITH);
            add(LootTables.STRONGHOLD_CORRIDOR);
            add(LootTables.DESERT_PYRAMID);
            add(LootTables.IGLOO_CHEST);
            add(LootTables.PILLAGER_OUTPOST);
            add(LootTables.BASTION_OTHER);
            add(LootTables.RUINED_PORTAL);
            add(LootTables.BASTION_HOGLIN_STABLE);
            add(LootTables.UNDERWATER_RUIN_BIG);
            add(LootTables.UNDERWATER_RUIN_SMALL);
        }});
        // 史诗 有较好的装备物资
        put(LootRarity.EPIC, new ArrayList<>() {{
            add(LootTables.SHIPWRECK_TREASURE);
            add(LootTables.BURIED_TREASURE);
            add(LootTables.SIMPLE_DUNGEON);
            add(LootTables.STRONGHOLD_LIBRARY);
        }});
        // 传说 有钻石物品和丰富物资
        put(LootRarity.LEGENDARY, new ArrayList<>() {{
            add(LootTables.END_CITY_TREASURE);
            add(LootTables.BASTION_TREASURE);
            add(LootTables.ANCIENT_CITY);
        }});
    }};

    @NonNull
    public LootTable randomLootTable(LootRarity lootRarity) {
        List<LootTables> lootTablesList = lootMap.get(lootRarity);
        if (lootTablesList == null) {
            Notifier.error("不支持的战利品稀有度：" + lootRarity.name());
            return LootTables.EMPTY.getLootTable();
        }
        return lootTablesList.get((int) (lootTablesList.size() * Math.random())).getLootTable();
    }
}
