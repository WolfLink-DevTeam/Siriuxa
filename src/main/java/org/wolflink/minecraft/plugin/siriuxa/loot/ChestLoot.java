package org.wolflink.minecraft.plugin.siriuxa.loot;

import org.bukkit.block.Chest;
import org.wolflink.common.ioc.IOC;

public class ChestLoot {

    private final Chest chest;
    private final LootRarity lootRarity;

    public ChestLoot(Chest chest, LootRarity lootRarity) {
        this.chest = chest;
        this.lootRarity = lootRarity;
    }

    public ChestLoot(Chest chest) {
        this.chest = chest;
        this.lootRarity = LootRarity.getRandom();
    }

    private static final String NAME_FORMAT = "§8[ §r%s §8] §0战利品箱";

    public void applyLootTable() {
        chest.setLootTable(IOC.getBean(ChestLootTableProxy.class).randomLootTable(lootRarity));
        chest.setCustomName(NAME_FORMAT.formatted(lootRarity.getDisplayName()));
        chest.update();
    }
}
