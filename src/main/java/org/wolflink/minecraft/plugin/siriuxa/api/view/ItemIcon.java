package org.wolflink.minecraft.plugin.siriuxa.api.view;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemIcon extends Icon{
    private final ItemStack itemStack;
    /**
     * 设置为 小于等于0 则该图标不需要刷新
     */
    public ItemIcon(ItemStack itemStack) {
        super(0);
        this.itemStack = itemStack;
    }

    @Override
    protected @NonNull ItemStack createIcon() {
        if(itemStack == null) return new ItemStack(Material.AIR);
        else return itemStack;
    }

    @Override
    public void leftClick(Player player) {

    }

    @Override
    public void rightClick(Player player) {

    }
}
