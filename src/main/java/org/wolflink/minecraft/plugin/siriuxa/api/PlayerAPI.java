package org.wolflink.minecraft.plugin.siriuxa.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;

@Singleton
public class PlayerAPI {
    public void setExp(Player player,final int totalExp) {
        int exp = totalExp;
        Notifier.debug("玩家 " + player.getName() + " 的经验已被重置为 0，即将发放经验：" + exp);
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);
        while (player.getExpToLevel() <= exp) {
            exp -= player.getExpToLevel();
            player.setLevel(player.getLevel() + 1);
        }
        player.setExp((float) exp / player.getExpToLevel());
        player.setTotalExperience(totalExp);
        Notifier.debug("玩家 " + player.getName() + " 当前等级 " + player.getLevel() + " 当前经验比例 " + String.format("%.2f", player.getExp()));
    }

    public void addExp(Player player, int exp) {
        Bukkit.dispatchCommand(Siriuxa.getInstance().getServer().getConsoleSender(), "experience add " + player.getName() + " " + exp);
    }
}
