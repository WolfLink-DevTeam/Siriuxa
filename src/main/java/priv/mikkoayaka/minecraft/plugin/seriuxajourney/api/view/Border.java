package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Border extends ItemIcon {

    public Border() {
        super(false);
    }

    @Override
    public void leftClick(Player player) { }

    @Override
    public void rightClick(Player player) { }
    @Override
    protected @NotNull ItemStack createIcon() {
        return fastCreateItemStack(Material.ORANGE_STAINED_GLASS_PANE,1," ");
    }
}
