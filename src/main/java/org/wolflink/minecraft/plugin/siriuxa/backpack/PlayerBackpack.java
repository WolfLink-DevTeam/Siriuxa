package org.wolflink.minecraft.plugin.siriuxa.backpack;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.PlayerAPI;
import org.wolflink.minecraft.wolfird.framework.config.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 玩家背包
 * 包含 帽子，胸甲，护腿，鞋子 副手
 * 经验等级 经验值
 * 背包中36格物品
 */
@Data
@NoArgsConstructor
public final class PlayerBackpack implements ConfigurationSerializable {
    @Getter
    private static PlayerBackpack emptyBackpack = new PlayerBackpack();
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack offhand;
    private int totalExp = 0; // 经验合计
    private List<ItemStack> items; // 背包物品

    public PlayerBackpack(Player player) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment != null) {
            helmet = equipment.getHelmet() == null ? null : equipment.getHelmet().clone();
            chestplate = equipment.getChestplate() == null ? null : equipment.getChestplate().clone();
            leggings = equipment.getLeggings() == null ? null : equipment.getLeggings().clone();
            boots = equipment.getBoots() == null ? null : equipment.getBoots().clone();
            offhand = equipment.getItemInOffHand().clone();
        } else Notifier.error("玩家" + player.getName() + "装备栏为空！");
        totalExp = player.getTotalExperience();
        items = new ArrayList<>();
        // 拷贝背包
        Inventory playerInv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = playerInv.getItem(i);
            items.add(itemStack == null ? null : itemStack.clone());
        }
    }

    public PlayerBackpack(Map<String, Object> map) {
        helmet = (ItemStack) map.get("helmet");
        chestplate = (ItemStack) map.get("chestplate");
        leggings = (ItemStack) map.get("leggings");
        boots = (ItemStack) map.get("boots");
        offhand = (ItemStack) map.get("offhand");
        totalExp = (int) map.get("totalExp");
        items = (List<ItemStack>) map.get("items");
    }

    public static PlayerBackpack deserialize(Map<String, Object> map) {
        return new PlayerBackpack(map);
    }

    void apply(Player player) {
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
        IOC.getBean(PlayerAPI.class).setExp(player,totalExp);
        Inventory inventory = player.getInventory();
        if (items != null) for (int i = 0; i < 36; i++) {
            inventory.setItem(i, i < items.size() ? items.get(i) : null);
        }
        else player.getInventory().clear();
        Notifier.debug("背包信息已应用至" + player.getName());
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("helmet", helmet);
        map.put("chestplate", chestplate);
        map.put("leggings", leggings);
        map.put("boots", boots);
        map.put("offhand", offhand);
        map.put("totalExp", totalExp);
        map.put("items", items);
        return map;
    }
}
