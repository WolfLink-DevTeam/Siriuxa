package org.wolflink.minecraft.plugin.siriuxa.backpack;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.file.database.InventoryDB;

import java.util.List;

@Singleton
public class InvBackupService {

    @Inject
    private InventoryDB inventoryDB;

    /**
     * 保存主要背包
     * 玩家在主城，地皮世界等地方使用的都是主要背包
     */
    public Result saveMainInv(Player player) {
        return saveMainInv(player, new PlayerBackpack(player));
    }

    public Result saveMainInv(Player player, PlayerBackpack playerBackpack) {
        inventoryDB.saveMain(player, playerBackpack);
        return new Result(true, "保存成功。");
    }
    public Result giveFiveSlotBackpack(Player player) {
        EnderBackpack enderBackpack = inventoryDB.loadFiveSlot(player);
        enderBackpack.give(player);
        return new Result(true,"发放成功。");
    }

    /**
     * 结束任务后更新背包状态
     */
    public Result updateFiveSlotBackpack(OfflinePlayer offlinePlayer, boolean taskResult) {
        if(taskResult) return clearFiveSlotBackpack(offlinePlayer);
        // 任务失败，清理未锁定物品
        else return clearUnlockedFiveSlotBackpack(offlinePlayer);
    }
    public Result clearUnlockedFiveSlotBackpack(OfflinePlayer offlinePlayer) {
        EnderBackpack enderBackpack = inventoryDB.loadFiveSlot(offlinePlayer);
        List<Boolean> lockedSlots = enderBackpack.getLockedSlots();
        if (!lockedSlots.get(0)) enderBackpack.setHelmet(null);
        if (!lockedSlots.get(1)) enderBackpack.setChestplate(null);
        if (!lockedSlots.get(2)) enderBackpack.setLeggings(null);
        if (!lockedSlots.get(3)) enderBackpack.setBoots(null);
        if (!lockedSlots.get(4)) enderBackpack.setItem(null);
        // 清理格子锁定状态
        enderBackpack.resetLockedSlots();
        saveFiveSlotBackpack(offlinePlayer, enderBackpack);
        return new Result(true,"未锁定物品清理成功。");
    }
    public Result clearFiveSlotBackpack(OfflinePlayer offlinePlayer) {
        EnderBackpack enderBackpack = inventoryDB.loadFiveSlot(offlinePlayer);
        enderBackpack.clear();
        enderBackpack.resetLockedSlots();
        saveFiveSlotBackpack(offlinePlayer, enderBackpack);
        return new Result(true,"清理成功。");
    }
    public Result saveFiveSlotBackpack(OfflinePlayer offlinePlayer, EnderBackpack enderBackpack) {
        inventoryDB.saveFiveSlot(offlinePlayer, enderBackpack);
        return new Result(true, "保存成功。");
    }

    public Result applyMainInv(Player player) {
        PlayerBackpack playerBackpack = inventoryDB.loadMain(player);
        if (playerBackpack == null) return new Result(false, "未获取到玩家主背包数据。");
        playerBackpack.apply(player);
        return new Result(true, "背包数据已应用至玩家。");
    }

    public Result applyInv(Player player, PlayerBackpack playerBackpack) {
        playerBackpack.apply(player);
        return new Result(true, "背包数据已应用至玩家。");
    }
}
