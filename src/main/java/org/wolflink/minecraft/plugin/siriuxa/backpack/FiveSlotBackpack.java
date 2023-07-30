package org.wolflink.minecraft.plugin.siriuxa.backpack;

import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 只包含五格物品
 * 分别是：头盔，护甲，护腿，靴子
 * 以及任意一格物品
 */
@Data
public class FiveSlotBackpack implements ConfigurationSerializable {

    @Getter
    private static FiveSlotBackpack defaultBackpack = new FiveSlotBackpack();
    static {
        defaultBackpack.helmet = new ItemStack(Material.LEATHER_HELMET);
        defaultBackpack.chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        defaultBackpack.leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        defaultBackpack.boots = new ItemStack(Material.LEATHER_BOOTS);
        defaultBackpack.item = new ItemStack(Material.BREAD,8);
    }

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack item;

    private List<Boolean> lockedSlots;

    public FiveSlotBackpack() {
        helmet = null;
        chestplate = null;
        leggings = null;
        boots = null;
        item = null;
        resetLockedSlots();
    }
    public int getLockedSlotAmount() {
        int amount = 0;
        for (boolean b : lockedSlots) {
            if(b) amount++;
        }
        return amount;
    }
    private static final int[] LOCK_PRICES = new int[]{30,60,90,180};
    private static final int MAX_LOCKED_SLOTS_AMOUNT = 4;
    public boolean canLock() {
        return getLockedSlotAmount() < MAX_LOCKED_SLOTS_AMOUNT;
    }
    public int getLockPrice() {
        return LOCK_PRICES[getLockedSlotAmount()];
    }
    public boolean isEmpty() {
        return helmet == null && chestplate == null && leggings == null && boots == null && item == null;
    }
    public void give(Player player) {
        Inventory inventory = player.getInventory();

        if(helmet == null) inventory.addItem(defaultBackpack.helmet);
        else inventory.addItem(helmet);

        if(chestplate == null) inventory.addItem(defaultBackpack.chestplate);
        else inventory.addItem(chestplate);

        if(leggings == null) inventory.addItem(defaultBackpack.leggings);
        else inventory.addItem(leggings);

        if(boots == null) inventory.addItem(defaultBackpack.boots);
        else inventory.addItem(boots);

        if(item == null) inventory.addItem(defaultBackpack.item);
        else inventory.addItem(item);
    }
    public void clear() {
        helmet = null;
        chestplate = null;
        leggings = null;
        boots = null;
        item = null;
        resetLockedSlots();
    }
    public void resetLockedSlots() {
        lockedSlots = Stream.of(false,false,false,false,false).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = new HashMap<>();
        map.put("helmet",helmet);
        map.put("chestplate",chestplate);
        map.put("leggings",leggings);
        map.put("boots",boots);
        map.put("item",item);
        map.put("lockedSlots",lockedSlots);
        return map;
    }
    public FiveSlotBackpack(Map<String,Object> map) {
        helmet = (ItemStack) map.get("helmet");
        chestplate = (ItemStack) map.get("chestplate");
        leggings = (ItemStack) map.get("leggings");
        boots = (ItemStack) map.get("boots");
        item = (ItemStack) map.get("item");
        lockedSlots = (List<Boolean>) map.get("lockedSlots");
    }
    public static FiveSlotBackpack deserialize(Map<String,Object> map) {
        return new FiveSlotBackpack(map);
    }
}
