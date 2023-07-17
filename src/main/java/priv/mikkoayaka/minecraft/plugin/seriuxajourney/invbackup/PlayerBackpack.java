package priv.mikkoayaka.minecraft.plugin.seriuxajourney.invbackup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.config.Json;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.SerializeAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

/**
 * 玩家背包
 * 包含 帽子，胸甲，护腿，鞋子 副手
 * 经验等级 经验值
 * 背包中36格物品
 */
@Data
@Json
@NoArgsConstructor
public class PlayerBackpack {
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
            helmet = equipment.getHelmet();
            chestplate = equipment.getChestplate();
            leggings = equipment.getLeggings();
            boots = equipment.getBoots();
            offhand = equipment.getItemInOffHand();
        } else Notifier.error("玩家" + player.getName() + "装备栏为空！");
        level = player.getLevel();
        exp = player.getExp();
        items = new ItemStack[36];
        // 拷贝背包
        Inventory playerInv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            items[i] = playerInv.getItem(i);
        }
    }

    public void apply(Player player) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null) {
            Notifier.error("玩家" + player.getName() + "装备栏为空！");
            return;
        }
        if (helmet != null) equipment.setHelmet(helmet);
        if (chestplate != null) equipment.setChestplate(chestplate);
        if (leggings != null) equipment.setLeggings(leggings);
        if (boots != null) equipment.setBoots(boots);
        if (offhand != null) equipment.setItemInOffHand(offhand);
        player.setLevel(level);
        player.setExp(exp);
        Inventory inventory = player.getInventory();
        if (items != null) for (int i = 0; i < 36; i++) {
            inventory.setItem(i, items[i]);
        }
        Notifier.debug("背包信息已应用至" + player.getName());
    }

    public JsonObject toJsonObject() {
        SerializeAPI serializeAPI = IOC.getBean(SerializeAPI.class);
        JsonObject result = new JsonObject();
        result.addProperty("helmet", serializeAPI.itemStack(helmet));
        result.addProperty("chestplate", serializeAPI.itemStack(chestplate));
        result.addProperty("leggings", serializeAPI.itemStack(leggings));
        result.addProperty("boots", serializeAPI.itemStack(boots));
        result.addProperty("offhand", serializeAPI.itemStack(offhand));
        result.addProperty("level", level);
        result.addProperty("exp", exp);
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < 36; i++) {
            jsonArray.add(serializeAPI.itemStack(items[i]));
        }
        result.add("items", jsonArray);
        return result;
    }

    public static PlayerBackpack fromJsonObject(JsonObject jsonObject) {
        SerializeAPI serializeAPI = IOC.getBean(SerializeAPI.class);
        ItemStack[] items = new ItemStack[36];
        JsonArray jsonArray = jsonObject.getAsJsonArray("items");
        for (int i = 0; i < 36; i++) {
            items[i] = serializeAPI.itemStack(jsonArray.get(i).getAsString());
        }
        PlayerBackpack playerBackpack = new PlayerBackpack();
        playerBackpack.setHelmet(serializeAPI.itemStack(jsonObject.get("helmet").getAsString()));
        playerBackpack.setChestplate(serializeAPI.itemStack(jsonObject.get("chestplate").getAsString()));
        playerBackpack.setLeggings(serializeAPI.itemStack(jsonObject.get("leggings").getAsString()));
        playerBackpack.setBoots(serializeAPI.itemStack(jsonObject.get("boots").getAsString()));
        playerBackpack.setOffhand(serializeAPI.itemStack(jsonObject.get("offhand").getAsString()));
        playerBackpack.setLevel(jsonObject.get("level").getAsInt());
        playerBackpack.setExp(jsonObject.get("exp").getAsFloat());
        playerBackpack.setItems(items);
        return playerBackpack;
    }

    @Getter
    private static PlayerBackpack emptyBackpack = new PlayerBackpack();
}
