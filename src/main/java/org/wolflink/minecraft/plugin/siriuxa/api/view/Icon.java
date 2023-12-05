package org.wolflink.minecraft.plugin.siriuxa.api.view;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class Icon {
    private final long refreshTick;
    private ItemStack itemStackCache;

    /**
     * 设置为 小于等于0 则该图标不需要刷新
     */
    protected Icon(long refreshTick) {
        this.refreshTick = refreshTick;
    }

    @NonNull
    public ItemStack getIcon() {
        if (refreshTick > 0) return createIcon();
        else if (itemStackCache == null) itemStackCache = createIcon();
        return itemStackCache;
    }

    @NonNull
    protected abstract ItemStack createIcon();

    protected ItemStack fastCreateItemStack(Material material, int count, String displayName, List<String> lores) {
        ItemStack itemStack = new ItemStack(material, count);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) throw new NullPointerException("ItemMeta is null");
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    protected ItemStack fastCreateItemStack(Material material, int count, String displayName, String... lores) {
        List<String> loreList;
        if (lores == null || lores.length == 0) loreList = new ArrayList<>();
        else loreList = Arrays.asList(lores);
        return fastCreateItemStack(material, count, displayName, loreList);
    }

    public abstract void leftClick(Player player);

    public abstract void rightClick(Player player);
}
