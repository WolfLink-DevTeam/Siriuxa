package org.wolflink.minecraft.plugin.siriuxa.menu.task;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.view.BorderIcon;
import org.wolflink.minecraft.plugin.siriuxa.api.view.ItemIcon;
import org.wolflink.minecraft.plugin.siriuxa.api.view.StaticMenu;
import org.wolflink.minecraft.plugin.siriuxa.backpack.FiveSlotBackpack;
import org.wolflink.minecraft.plugin.siriuxa.file.database.InventoryDB;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FiveSlotBackpackMenu extends StaticMenu {

    private static final ItemStack anythingItemIcon = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
    static {
        ItemMeta itemMeta = anythingItemIcon.getItemMeta();
        if(itemMeta != null) {
            itemMeta.setDisplayName("§8[ §a可放入任何物品 §8]");
            itemMeta.setLore(Stream.of(" ","  §7可以是工具，药水，食物，护身符...","  §7当然，潜影盒等容器除外。"," ").toList());
            anythingItemIcon.setItemMeta(itemMeta);
        }
    }

    public FiveSlotBackpackMenu(UUID ownerUuid) {
        super(ownerUuid, "§0§l安全背包", 27, Stream.of(20,21,22,23,24).collect(Collectors.toSet()));
    }
    private final FiveSlotBackpack fiveSlotBackpack = IOC.getBean(InventoryDB.class).loadFiveSlot(getOfflineOwner());

    @Override
    protected void overrideIcons() {
        BorderIcon borderIcon = IOC.getBean(BorderIcon.class);
        Stream.of(9,10,11,12,13,14,15,16,17).forEach(i -> setIcon(i,borderIcon));
        Player player = getOwner();
        if(player == null || !player.isOnline())return;
        FiveSlotBackpack defaultBackpack = FiveSlotBackpack.getDefaultBackpack();
        setIcon(2,new ItemIcon(defaultBackpack.getHelmet()));
        setIcon(3,new ItemIcon(defaultBackpack.getChestplate()));
        setIcon(4,new ItemIcon(defaultBackpack.getLeggings()));
        setIcon(5,new ItemIcon(defaultBackpack.getBoots()));
        setIcon(6,new ItemIcon(anythingItemIcon));
        setIcon(20, new ItemIcon(fiveSlotBackpack.getHelmet()));
        setIcon(21, new ItemIcon(fiveSlotBackpack.getChestplate()));
        setIcon(22, new ItemIcon(fiveSlotBackpack.getLeggings()));
        setIcon(23, new ItemIcon(fiveSlotBackpack.getBoots()));
        setIcon(24, new ItemIcon(fiveSlotBackpack.getItem()));
    }
    @Override
    public void onClose(Player player) {

        ItemStack helmet = inventory.getItem(20);
        if(helmet != null) {
            if(helmet.getType().name().endsWith("_HELMET")) fiveSlotBackpack.setHelmet(helmet);
            else {
                Notifier.chat("你在应该放置头盔的地方错误的放入了物品："+helmet.getType().name().toLowerCase()+"！物品已退回至背包。",player);
                player.getInventory().addItem(helmet);
            }
        }

        ItemStack chestplate = inventory.getItem(21);
        if(chestplate != null) {
            if(chestplate.getType().name().endsWith("_CHESTPLATE")) fiveSlotBackpack.setChestplate(chestplate);
            else {
                Notifier.chat("你在应该放置胸甲的地方错误的放入了物品："+chestplate.getType().name().toLowerCase()+"！物品已退回至背包。",player);
                player.getInventory().addItem(chestplate);
            }
        }

        ItemStack leggings = inventory.getItem(22);
        if(leggings != null) {
            if(leggings.getType().name().endsWith("_LEGGINGS")) fiveSlotBackpack.setLeggings(leggings);
            else {
                Notifier.chat("你在应该放置护腿的地方错误的放入了物品："+leggings.getType().name().toLowerCase()+"！物品已退回至背包。",player);
                player.getInventory().addItem(leggings);
            }
        }

        ItemStack boots = inventory.getItem(23);
        if(boots != null) {
            if(boots.getType().name().endsWith("_BOOTS")) fiveSlotBackpack.setBoots(boots);
            else {
                Notifier.chat("你在应该放置鞋子的地方错误的放入了物品："+boots.getType().name().toLowerCase()+"！物品已退回至背包。",player);
                player.getInventory().addItem(boots);
            }
        }

        ItemStack item = getIcon(24).getIcon();
        fiveSlotBackpack.setItem(item);
    }
}
