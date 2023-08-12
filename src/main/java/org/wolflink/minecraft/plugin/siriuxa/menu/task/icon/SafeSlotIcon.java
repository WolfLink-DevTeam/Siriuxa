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
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerVariableDB;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerVariables;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.TaskMenu;

public class SafeSlotIcon extends Icon {

    private static final int MAX_SLOTS = 5;
    private static final int[] SLOT_PRICE = new int[]{30,60,90,120,150};
    private final TaskMenu taskMenu;
    public SafeSlotIcon(TaskMenu taskMenu) {
        super(10);
        this.taskMenu = taskMenu;
    }

    public int getSlotAmount() {
        return IOC.getBean(PlayerVariableDB.class).get(taskMenu.getOfflineOwner()).getSafeSlotAmount();
    }
    @Override
    protected @NonNull ItemStack createIcon() {
        int slotAmount = getSlotAmount();
        Material mat;
        int amount = slotAmount;
        String price;
        if(slotAmount < MAX_SLOTS) price = "§a"+SLOT_PRICE[slotAmount]+" §6麦穗";
        else price = "§c已达上限，无法购买";
        if(amount == 0) amount = 1;
        if(slotAmount == 0) mat = Material.ENDER_PEARL;
        else mat = Material.ENDER_EYE;
        return fastCreateItemStack(mat,amount,"§8[ §d末影祝福 §8] §f已购买 §a"+slotAmount+" §f格",
                " ",
                "  §7源于末地的奇异力量能够保护你的贵重物品以防丢失，",
                "  §7但显然目前这种技术仍无法被广泛应用，使用代价昂贵。",
                "  ",
                "  §7购买后如果任务失败可以拿回对应数量的物资",
                "  §7任务成功后祝福也会失效",
                "  ",
                "  §f价格 §r"+price,
                "  ",
                "  §a右键 §d购买祝福",
                " ");
    }

    @Override
    public void leftClick(Player player) {

    }

    @Override
    public void rightClick(Player player) {
        int slotAmount = getSlotAmount();
        if(slotAmount >= MAX_SLOTS) return;
        VaultAPI vaultAPI = IOC.getBean(VaultAPI.class);
        int lockPrice = SLOT_PRICE[slotAmount];
        if(vaultAPI.getEconomy(player) < lockPrice) return;
        vaultAPI.takeEconomy(player,lockPrice);
        PlayerVariableDB db = IOC.getBean(PlayerVariableDB.class);
        PlayerVariables playerVariables = db.get(taskMenu.getOfflineOwner());
        playerVariables.setSafeSlotAmount(playerVariables.getSafeSlotAmount()+1);
        db.save(taskMenu.getOfflineOwner(), playerVariables);
        Notifier.chat("§d远古的咒语在你耳边吟唱...末影祝福开始生效了！",player);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE,1.2f,0.7f);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES,1f,1f);
    }
}
