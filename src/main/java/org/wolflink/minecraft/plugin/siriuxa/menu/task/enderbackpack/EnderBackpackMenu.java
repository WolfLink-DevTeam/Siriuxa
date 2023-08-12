package org.wolflink.minecraft.plugin.siriuxa.menu.task.enderbackpack;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.view.BorderIcon;
import org.wolflink.minecraft.plugin.siriuxa.api.view.DynamicMenu;
import org.wolflink.minecraft.plugin.siriuxa.api.view.ItemIcon;
import org.wolflink.minecraft.plugin.siriuxa.backpack.EnderBackpack;
import org.wolflink.minecraft.plugin.siriuxa.backpack.InvBackupService;
import org.wolflink.minecraft.plugin.siriuxa.file.database.InventoryDB;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnderBackpackMenu extends DynamicMenu {

    public EnderBackpackMenu(UUID ownerUuid) {
        super(ownerUuid, "§0§l末影背包", 27,0, Stream.of(19,20,21,22,23,24,25).collect(Collectors.toSet()));
    }

    public EnderBackpack getEnderBackpack() {
        return IOC.getBean(InventoryDB.class).loadEnderBackpack(getOfflineOwner());
    }
    public void saveEnderBackpack(EnderBackpack enderBackpack) {
        Player player = getOwner();
        if(player == null || !player.isOnline())return;
        IOC.getBean(InvBackupService.class).saveEnderBackpack(getOwner(), enderBackpack);
    }

    @Override
    protected void overrideIcons() {
        BorderIcon borderIcon = IOC.getBean(BorderIcon.class);
        Stream.of(9,10,11,12,13,14,15,16,17).forEach(i -> setIcon(i,borderIcon));
        Player player = getOwner();
        if(player == null || !player.isOnline())return;
        setIcon(1,IOC.getBean(HelmetIcon.class));
        setIcon(2,IOC.getBean(ChestplateIcon.class));
        setIcon(3,IOC.getBean(LeggingsIcon.class));
        setIcon(4,IOC.getBean(BootsIcon.class));
        setIcon(5,IOC.getBean(WeaponIcon.class));
        setIcon(6,IOC.getBean(ToolIcon.class));
        setIcon(7,IOC.getBean(AnythingIcon.class));

        EnderBackpack enderBackpack = getEnderBackpack();
        setIcon(19, new ItemIcon(enderBackpack.getHelmet()));
        setIcon(20, new ItemIcon(enderBackpack.getChestplate()));
        setIcon(21, new ItemIcon(enderBackpack.getLeggings()));
        setIcon(22, new ItemIcon(enderBackpack.getBoots()));
        setIcon(23, new ItemIcon(enderBackpack.getWeapon()));
        setIcon(24, new ItemIcon(enderBackpack.getTool()));
        setIcon(25, new ItemIcon(enderBackpack.getItem()));
    }
    private static final Set<String> containerSuffix = new HashSet<>();
    static {
        containerSuffix.add("SHULKER_BOX");
        containerSuffix.add("BUNDLE");
    }
    @Override
    public void onClose(Player player) {
        EnderBackpack enderBackpack = getEnderBackpack();
        // 头盔
        {
            ItemStack helmet = inventory.getItem(19);
            if(helmet != null && helmet.getType() != Material.AIR) {
                if(helmet.getType().name().endsWith("_HELMET")) enderBackpack.setHelmet(helmet);
                else {
                    Notifier.chat("你在应该放置头盔的地方错误的放入了物品："+helmet.getType().name().toLowerCase()+"！物品已退回至背包。",player);
                    enderBackpack.setHelmet(null);
                    player.getInventory().addItem(helmet);
                    setIcon(19,null);
                    inventory.setItem(19,null);
                }
            } else enderBackpack.setHelmet(null);
        }
        // 胸甲
        {
            ItemStack chestplate = inventory.getItem(20);
            if(chestplate != null && chestplate.getType() != Material.AIR) {
                if(chestplate.getType().name().endsWith("_CHESTPLATE")) enderBackpack.setChestplate(chestplate);
                else {
                    Notifier.chat("你在应该放置胸甲的地方错误的放入了物品："+chestplate.getType().name().toLowerCase()+"！物品已退回至背包。",player);
                    player.getInventory().addItem(chestplate);
                    enderBackpack.setChestplate(null);
                    setIcon(20,null);
                    inventory.setItem(20,null);
                }
            } else enderBackpack.setChestplate(null);
        }
        // 护腿
        {
            ItemStack leggings = inventory.getItem(21);
            if(leggings != null && leggings.getType() != Material.AIR) {
                if(leggings.getType().name().endsWith("_LEGGINGS")) enderBackpack.setLeggings(leggings);
                else {
                    Notifier.chat("你在应该放置护腿的地方错误的放入了物品："+leggings.getType().name().toLowerCase()+"！物品已退回至背包。",player);
                    player.getInventory().addItem(leggings);
                    enderBackpack.setLeggings(null);
                    setIcon(21,null);
                    inventory.setItem(21,null);
                }
            } else enderBackpack.setLeggings(null);
        }
        // 鞋子
        {
            ItemStack boots = inventory.getItem(22);
            if(boots != null && boots.getType() != Material.AIR) {
                if(boots.getType().name().endsWith("_BOOTS")) enderBackpack.setBoots(boots);
                else {
                    Notifier.chat("你在应该放置鞋子的地方错误的放入了物品："+boots.getType().name().toLowerCase()+"！物品已退回至背包。",player);
                    player.getInventory().addItem(boots);
                    enderBackpack.setBoots(null);
                    setIcon(22,null);
                    inventory.setItem(22,null);
                }
            } else enderBackpack.setBoots(null);
        }
        // 武器
        {
            ItemStack weapon = inventory.getItem(23);
            if(weapon != null && weapon.getType() != Material.AIR) {
                String weaponName = weapon.getType().name();
                if(weaponName.endsWith("_SWORD") || weaponName.endsWith("_AXE") || weaponName.equals("BOW") || weaponName.equals("CROSSBOW") || weaponName.equals("TRIDENT")) enderBackpack.setWeapon(weapon);
                else {
                    Notifier.chat("你在应该放置武器的地方错误的放入了物品："+weaponName.toLowerCase()+"！物品已退回至背包。",player);
                    player.getInventory().addItem(weapon);
                    enderBackpack.setWeapon(null);
                    setIcon(23,null);
                    inventory.setItem(23,null);
                }
            } else enderBackpack.setWeapon(null);
        }
        // 工具
        {
            ItemStack tool = inventory.getItem(24);
            if(tool != null && tool.getType() != Material.AIR) {
                String toolName = tool.getType().name();
                if(toolName.endsWith("_PICKAXE") || toolName.endsWith("_AXE") || toolName.endsWith("_HOE") || toolName.endsWith("_SHOVEL")) enderBackpack.setTool(tool);
                else {
                    Notifier.chat("你在应该放置工具的地方错误的放入了物品："+toolName.toLowerCase()+"！物品已退回至背包。",player);
                    player.getInventory().addItem(tool);
                    enderBackpack.setTool(null);
                    setIcon(24,null);
                    inventory.setItem(24,null);
                }
            } else enderBackpack.setTool(null);
        }
        // 通用
        {
            ItemStack item = inventory.getItem(25);
            if(item != null && item.getType() != Material.AIR) {
                boolean isContainer = false;
                for (String containerName : containerSuffix) {
                    if(item.getType().name().endsWith(containerName)){
                        isContainer = true;
                        break;
                    }
                }
                if(isContainer) {
                    Notifier.chat("你不可以携带容器进入任务！",player);
                    player.getInventory().addItem(item);
                    enderBackpack.setItem(null);
                    setIcon(25,null);
                    inventory.setItem(25,null);
                }
                else enderBackpack.setItem(item);
            }
            else enderBackpack.setItem(null);
            saveEnderBackpack(enderBackpack);
        }
    }
}
