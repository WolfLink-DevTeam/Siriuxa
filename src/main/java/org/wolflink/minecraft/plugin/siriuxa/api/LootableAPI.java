package org.wolflink.minecraft.plugin.siriuxa.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.world.BlockAPI;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class LootableAPI {

    private final Map<ItemStack, Integer> map = new HashMap<>(){{
       put(new ItemStack(Material.APPLE,2),100);
       put(new ItemStack(Material.WOODEN_SWORD),100);
       put(new ItemStack(Material.STONE_SWORD),200);
       put(new ItemStack(Material.IRON_SWORD),50);
       put(new ItemStack(Material.LEATHER_HELMET),100);
       put(new ItemStack(Material.LEATHER_CHESTPLATE),100);
       put(new ItemStack(Material.LEATHER_LEGGINGS),100);
       put(new ItemStack(Material.LEATHER_BOOTS),100);
    }};

    private ItemStack randomItem() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int totalWeight = map.values().stream().reduce(0, Integer::sum);
        int randomInt = random.nextInt(totalWeight);
        for (Map.Entry<ItemStack, Integer> entry : map.entrySet()) {
            if (randomInt < entry.getValue()) return entry.getKey();
            randomInt -= entry.getValue();
        }
        // 不可触及
        Notifier.warn("物品类型随机时出现问题。");
        return new ItemStack(Material.AIR);
    }

    public void fillChest(Location center) {
        List<Location> locations = IOC.getBean(BlockAPI.class).searchBlock(Material.CHEST, center, 30);
        for (Location location : locations) {
            if(location.getBlock().getType() != Material.CHEST) continue;
            Chest chest = (Chest) location.getBlock().getState();
            Inventory inv = chest.getBlockInventory();
            
        }
    }
}
