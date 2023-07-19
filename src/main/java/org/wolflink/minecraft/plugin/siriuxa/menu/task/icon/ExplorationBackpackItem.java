package org.wolflink.minecraft.plugin.siriuxa.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Icon;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.ExplorationBackpackMenu;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExplorationBackpackItem extends Icon {

    private final ExplorationBackpackMenu explorationBackpackMenu;
    private final ItemStack itemStack;
    private final int index;
    public ExplorationBackpackItem(ExplorationBackpackMenu explorationBackpackMenu, int index, @Nullable ItemStack itemStack) {
        super(true);
        this.index = index;
        this.explorationBackpackMenu = explorationBackpackMenu;
        if(itemStack != null) {
            this.itemStack = itemStack.clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta == null) return;
            List<String> lores = new ArrayList<>(){{
               add(" ");
               add("  §f左键 §a选择物品");
               add("  §f右键 §c取消选择");
               add(" ");
            }};
            itemMeta.setLore(lores);
            itemStack.setItemMeta(itemMeta);
        }
        else this.itemStack = null;
    }

    @Override
    protected @NonNull ItemStack createIcon() {
        if(itemStack == null) return new ItemStack(Material.AIR);
        if(explorationBackpackMenu.containSlot(index)) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta == null) return itemStack;
            String itemName = itemMeta.getDisplayName();
            return fastCreateItemStack(Material.LIME_STAINED_GLASS_PANE,itemStack.getAmount(),"§a已选择 "+itemName,itemMeta.getLore());
        } else return itemStack;
    }

    @Override
    public void leftClick(Player player) {
        if(itemStack == null) return;
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES,1f,1f);
        explorationBackpackMenu.selectSlot(index);
    }

    @Override
    public void rightClick(Player player) {
        if(itemStack == null) return;
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1f,1f);
        explorationBackpackMenu.unselectSlot(index);
    }
}
