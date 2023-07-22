package org.wolflink.minecraft.plugin.siriuxa.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.wolflink.common.ioc.Singleton;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ItemAPI {

    //设置物品的名字并且根据品质物品的颜色（颜色代码通过枚举类来获得）
    public ItemStack setItemName(ItemStack itemStack, Tier tier, String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(tier.getColor() + name);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    //设置物品描述信息
    public ItemStack setItemLore(ItemStack itemStack, List<String> lores) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            Notifier.warn("物品 " + itemStack.getType().name() + " 没有 ItemMeta 属性");
            return itemStack;
        }
        List<String> finalLores = new ArrayList<>();
        finalLores.add(" "); //第一行为空白（blank）
        finalLores.addAll(lores.stream().map(str -> "§7" + str).toList()); //设置物品描述为默认颜色§7
        finalLores.add(" ");//最后一行为空白（不是null）
        itemMeta.setLore(finalLores);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
