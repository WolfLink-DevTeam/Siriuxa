package org.wolflink.minecraft.plugin.siriuxa.menu.task.enderbackpack;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.view.ItemIcon;
import org.wolflink.minecraft.plugin.siriuxa.backpack.EnderBackpack;

import java.util.stream.Stream;

@Singleton
public class WeaponIcon extends ItemIcon {
    private static final ItemStack itemStack = EnderBackpack.getDefaultBackpack().getWeapon().clone();

    static {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("§8[ §a可放入武器 §8]");
            itemMeta.setLore(Stream.of(" ", "  §7可以放入 §f剑,斧,弓,弩,三叉戟 §7类型的武器", " ").toList());
            itemStack.setItemMeta(itemMeta);
        }
    }

    public WeaponIcon() {
        super(itemStack);
    }
}
