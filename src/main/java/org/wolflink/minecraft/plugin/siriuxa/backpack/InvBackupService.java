package org.wolflink.minecraft.plugin.siriuxa.backpack;

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
        FiveSlotBackpack fiveSlotBackpack = inventoryDB.loadFiveSlot(player);
        fiveSlotBackpack.give(player);
        fiveSlotBackpack.clear();
        saveFiveSlotBackpack(player,fiveSlotBackpack);
        return new Result(true,"发放成功。");
    }

    /**
     * 结束任务后更新背包状态
     */
    public Result updateFiveSlotBackpack(Player player,boolean taskResult) {
        if(taskResult) return clearFiveSlotBackpack(player);
        // 任务失败，清理未锁定物品
        else return clearUnlockedFiveSlotBackpack(player);
    }
    public Result clearUnlockedFiveSlotBackpack(Player player) {
        FiveSlotBackpack fiveSlotBackpack = inventoryDB.loadFiveSlot(player);
        List<Boolean> lockedSlots = fiveSlotBackpack.getLockedSlots();
        if (!lockedSlots.get(0)) fiveSlotBackpack.setHelmet(null);
        if (!lockedSlots.get(1)) fiveSlotBackpack.setChestplate(null);
        if (!lockedSlots.get(2)) fiveSlotBackpack.setLeggings(null);
        if (!lockedSlots.get(3)) fiveSlotBackpack.setBoots(null);
        if (!lockedSlots.get(4)) fiveSlotBackpack.setItem(null);
        // 清理格子锁定状态
        fiveSlotBackpack.resetLockedSlots();
        saveFiveSlotBackpack(player,fiveSlotBackpack);
        return new Result(true,"未锁定物品清理成功。");
    }
    public Result clearFiveSlotBackpack(Player player) {
        FiveSlotBackpack fiveSlotBackpack = inventoryDB.loadFiveSlot(player);
        fiveSlotBackpack.clear();
        saveFiveSlotBackpack(player,fiveSlotBackpack);
        return new Result(true,"清理成功。");
    }
    public Result saveFiveSlotBackpack(Player player,FiveSlotBackpack fiveSlotBackpack) {
        inventoryDB.saveFiveSlot(player,fiveSlotBackpack);
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
