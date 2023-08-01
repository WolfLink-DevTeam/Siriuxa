package org.wolflink.minecraft.plugin.siriuxa.task.tasks.wheat.exploration.suppliesshop;

import org.bukkit.OfflinePlayer;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;

@Singleton
public class SuppliesShopService {

    public boolean canBuy(OfflinePlayer offlinePlayer,int productId) {
        return false;
    }
    public boolean haveBought(OfflinePlayer offlinePlayer,int productId) { return false; }

    public Result buy(OfflinePlayer offlinePlayer, int productId) {
        return new Result(true,"购买成功！物品将在稍后的任务中发放。");
    }
}
