package org.wolflink.minecraft.plugin.siriuxa.api.view;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.wolflink.common.ioc.Singleton;

@Singleton
public class BorderIcon extends Icon {

    public BorderIcon() {
        super(false);
    }

    @Override
    public void leftClick(Player player) {
        // do nothing
    }

    @Override
    public void rightClick(Player player) {
        // do nothing
    }

    @Override
    protected @NotNull ItemStack createIcon() {
        return fastCreateItemStack(Material.ORANGE_STAINED_GLASS_PANE, 1, " ");
    }
}
