package priv.mikkoayaka.minecraft.plugin.seriuxajourney.invbackup;

import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.Result;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.InventoryCache;

@Singleton
public class InvBackupService {

    @Inject
    private InventoryCache inventoryCache;

    /**
     * 保存主要背包
     * 玩家在主城，地皮世界等地方使用的都是主要背包
     */
    public Result saveMainInv(Player player) {
        inventoryCache.saveMainInv(player,new PlayerBackpack(player));
        return new Result(true,"保存成功。");
    }
    public Result loadMainInv(Player player) {
        PlayerBackpack playerBackpack = inventoryCache.getMainInv(player);
        if(playerBackpack == null) return new Result(false,"未获取到玩家主背包数据。");
        playerBackpack.apply(player);
        return new Result(true,"背包数据加载成功。");
    }
}
