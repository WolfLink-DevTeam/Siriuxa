package org.wolflink.minecraft.plugin.siriuxa.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Icon;
import org.wolflink.minecraft.plugin.siriuxa.backpack.FiveSlotBackpack;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.FiveSlotBackpackMenu;

public class EnderLock extends Icon {
    FiveSlotBackpackMenu menu;
    int index;
    public EnderLock(FiveSlotBackpackMenu menu, int index) {
        super(10);
        this.menu = menu;
        this.index = index;
    }

    @Override
    protected @NonNull ItemStack createIcon() {
        FiveSlotBackpack fiveSlotBackpack = menu.getFiveSlotBackpack();
        if(fiveSlotBackpack.getLockedSlots().get(index)) {
            return fastCreateItemStack(Material.ENDER_EYE,1,"§8[ §d末影祝福 §8] §a已生效",
                    " ",
                    "  §7源于末地的奇异力量能够保护你的贵重物品以防丢失，",
                    "  §7但显然目前这种技术仍无法被广泛应用，使用代价昂贵。",
                    " ");
        } else {
            if(fiveSlotBackpack.getLockedSlotAmount() < 4) return fastCreateItemStack(Material.ENDER_PEARL,1,"§8[ §d末影祝福 §8] §e可购买 §7(还可购买 §f"+(4-fiveSlotBackpack.getLockedSlotAmount())+" §7格)",
                    " ",
                    "  §7源于末地的奇异力量能够保护你的贵重物品以防丢失，",
                    "  §7但显然目前这种技术仍无法被广泛应用，使用代价昂贵。",
                    " ",
                    "  §f花费 §a"+fiveSlotBackpack.getLockPrice()+" §6麦穗 §f以购买 §d末影祝福",
                    " ");
            else return fastCreateItemStack(Material.BARRIER,1,"§8[ §d末影祝福 §8] §c不可购买",
                    " ",
                    "  §7源于末地的奇异力量能够保护你的贵重物品以防丢失，",
                    "  §7但显然目前这种技术仍无法被广泛应用，使用代价昂贵。",
                    " ");
        }
    }

    @Override
    public void leftClick(Player player) {
        FiveSlotBackpack fiveSlotBackpack = menu.getFiveSlotBackpack();
        if(fiveSlotBackpack.getLockedSlots().get(index)) return;
        if(!fiveSlotBackpack.canLock()) return;
        VaultAPI vaultAPI = IOC.getBean(VaultAPI.class);
        int lockPrice = fiveSlotBackpack.getLockPrice();
        if(vaultAPI.getEconomy(player) < lockPrice) return;
        vaultAPI.takeEconomy(player,lockPrice);
        fiveSlotBackpack.getLockedSlots().set(index,true);
        menu.saveFiveSlotBackpack(fiveSlotBackpack);
        Notifier.chat("§d远古的咒语在你耳边吟唱...末影祝福开始生效了！",player);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE,1.2f,0.7f);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES,1f,1f);
    }

    @Override
    public void rightClick(Player player) {

    }
}
