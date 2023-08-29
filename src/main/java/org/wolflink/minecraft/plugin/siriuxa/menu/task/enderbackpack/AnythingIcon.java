package org.wolflink.minecraft.plugin.siriuxa.menu.task.enderbackpack;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.view.ItemIcon;

import java.util.stream.Stream;

@Singleton
public class AnythingIcon extends ItemIcon {
    private static final ItemStack itemStack = new ItemStack(Material.NETHER_STAR);

    static {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§8[ §a可放入任何物品 §8]");
            itemMeta.setLore(Stream.of(" ", "  §7可以放入任何类型的物品...", "  §7当然，潜影盒等容器除外。", " ").toList());
            itemStack.setItemMeta(itemMeta);
        }
    }

    public AnythingIcon() {
        super(itemStack);
    }
}
