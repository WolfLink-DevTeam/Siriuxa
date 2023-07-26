package org.wolflink.minecraft.plugin.siriuxa.api;

import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Singleton;

@Singleton
public class PlayerAPI {
    public void setExp(Player player,int totalExp) {
        Notifier.debug("玩家 "+player.getName()+" 的经验已被重置为 0，即将发放经验："+totalExp);
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);
        while (player.getExpToLevel() <= totalExp) {
            totalExp -= player.getExpToLevel();
            player.setLevel(player.getLevel() + 1);
        }
        player.setExp((float) totalExp / player.getExpToLevel());
        Notifier.debug("玩家 "+player.getName()+" 当前等级 "+player.getLevel()+" 当前经验比例 "+String.format("%.2f",player.getExp()));
    }
}
