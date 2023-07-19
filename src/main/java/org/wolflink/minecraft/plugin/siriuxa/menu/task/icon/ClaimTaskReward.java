package org.wolflink.minecraft.plugin.siriuxa.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Icon;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.ExplorationBackpackMenu;

public class ClaimTaskReward extends Icon {
    private final ExplorationBackpackMenu explorationBackpackMenu;
    public ClaimTaskReward(ExplorationBackpackMenu explorationBackpackMenu) {
        super(false);
        this.explorationBackpackMenu = explorationBackpackMenu;
    }

    @Override
    protected @NonNull ItemStack createIcon() {
        int moreSlots = explorationBackpackMenu.getBringSlotAmount() - explorationBackpackMenu.getSelectedSlotAmount();
        if(moreSlots == 0) return fastCreateItemStack(Material.END_CRYSTAL,1,"§a§l带回物品",
                " ",
                "  §7点击即可将选中物品添加到背包",
                " ");
        else return fastCreateItemStack(Material.END_CRYSTAL,1,"§a§l带回物品",
                " ",
                "  §f还可以选择额外 §e"+moreSlots+" §f格物品",
                " ");
    }

    @Override
    public void leftClick(Player player) {
        explorationBackpackMenu.claimReward(player);
    }

    @Override
    public void rightClick(Player player) {

    }
}
