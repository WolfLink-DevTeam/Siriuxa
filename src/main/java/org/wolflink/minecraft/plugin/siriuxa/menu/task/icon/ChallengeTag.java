package org.wolflink.minecraft.plugin.siriuxa.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Icon;

public class ChallengeTag extends Icon {
    public ChallengeTag() {
        super(20);
    }

    @Override
    protected @NonNull ItemStack createIcon() {
        return fastCreateItemStack(Material.ENDER_CHEST,1,"§d潘多拉魔盒",
                " ",
                "  §c灾祸§7与§a希望§7。",
                "  §7这将会是一场试炼，准备好了吗？",
                "  "
                );
    }

    @Override
    public void leftClick(Player player) {
        new Result(false,"开发中，敬请期待！").show(player);
    }

    @Override
    public void rightClick(Player player) {

    }
}
