package org.wolflink.minecraft.plugin.siriuxa.invbackup;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;
import org.wolflink.minecraft.wolfird.framework.config.Json;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家背包
 * 包含 帽子，胸甲，护腿，鞋子 副手
 * 经验等级 经验值
 * 背包中36格物品
 */
@Data
@Json
@NoArgsConstructor
public class PlayerBackpack implements ConfigurationSerializable {
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack offhand;
    private int level = 0; // 等级
    private float exp = 0; // 当前等级的经验值
    private ItemStack[] items; // 背包物品

    public PlayerBackpack(Player player) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment != null) {
            if(equipment.getHelmet() == null) helmet = null;
            else helmet = equipment.getHelmet().clone();
            if(equipment.getChestplate() == null) chestplate = null;
            else chestplate = equipment.getChestplate().clone();
            if(equipment.getLeggings() == null) leggings = null;
            else leggings = equipment.getLeggings().clone();
            if(equipment.getBoots() == null) boots = null;
            else boots = equipment.getBoots().clone();
            offhand = equipment.getItemInOffHand().clone();
        } else Notifier.error("玩家" + player.getName() + "装备栏为空！");
        level = player.getLevel();
        exp = player.getExp();
        items = new ItemStack[36];
        // 拷贝背包
        Inventory playerInv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack is = playerInv.getItem(i);
            if(is != null) items[i] = is.clone();
            else items[i] = null;
        }
    }

    public void apply(Player player) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null) {
            Notifier.error("玩家" + player.getName() + "装备栏为空！");
            return;
        }
        equipment.setHelmet(helmet);
        equipment.setChestplate(chestplate);
        equipment.setLeggings(leggings);
        equipment.setBoots(boots);
        equipment.setItemInOffHand(offhand);
        player.setLevel(level);
        player.setExp(exp);
        Inventory inventory = player.getInventory();
        if (items != null) for (int i = 0; i < 36; i++) {
            inventory.setItem(i, items[i]);
        } else player.getInventory().clear();
        Notifier.debug("背包信息已应用至" + player.getName());
    }

    @Getter
    private static PlayerBackpack emptyBackpack = new PlayerBackpack();

    public PlayerBackpack(Map<String,Object> map) {
        helmet = (ItemStack) map.get("helmet");
        chestplate = (ItemStack) map.get("chestplate");
        leggings = (ItemStack) map.get("leggings");
        boots = (ItemStack) map.get("boots");
        offhand = (ItemStack) map.get("offhand");
        level = (int) map.get("level");
        exp = (float) map.get("exp");
        items = (ItemStack[]) map.get("items");
    }
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = new HashMap<>();
        map.put("helmet",helmet);
        map.put("chestplate",chestplate);
        map.put("leggings",leggings);
        map.put("boots",boots);
        map.put("offhand",offhand);
        map.put("level",level);
        map.put("exp",exp);
        map.put("items",items);
        return map;
    }
    public static PlayerBackpack deserialize(Map<String,Object> map) {
        return new PlayerBackpack(map);
    }
}
