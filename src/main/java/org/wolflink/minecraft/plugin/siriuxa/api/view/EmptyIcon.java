package org.wolflink.minecraft.plugin.siriuxa.api.view;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.wolflink.common.ioc.Singleton;

@Singleton
public class EmptyIcon extends Icon {
    public EmptyIcon() {
        super(false);
    }

    @Override
    public void leftClick(Player player) {
    }

    @Override
    public void rightClick(Player player) {
    }

    @Override
    protected @NotNull ItemStack createIcon() {
        return new ItemStack(Material.AIR);
    }
}
