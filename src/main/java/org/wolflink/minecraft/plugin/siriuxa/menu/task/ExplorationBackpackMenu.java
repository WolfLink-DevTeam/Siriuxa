package org.wolflink.minecraft.plugin.siriuxa.menu.task;

import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.PlayerAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.view.BorderIcon;
import org.wolflink.minecraft.plugin.siriuxa.api.view.DynamicMenu;
import org.wolflink.minecraft.plugin.siriuxa.api.view.EmptyIcon;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.DifficultyRepository;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerTaskRecord;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerVariableDB;
import org.wolflink.minecraft.plugin.siriuxa.file.database.TaskRecordDB;
import org.wolflink.minecraft.plugin.siriuxa.backpack.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.icon.ClaimTaskReward;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.icon.ExplorationBackpackItem;
import org.wolflink.minecraft.plugin.siriuxa.task.ornaments.OrnamentType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRelationProxy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class ExplorationBackpackMenu extends DynamicMenu {

    private final Set<Integer> selectedSlots = new HashSet<>();
    @Setter
    private PlayerTaskRecord playerTaskRecord = null;

    /**
     * 刷新周期设置小于0则为静态菜单
     * 静态菜单只会在打开时刷新一次
     */
    public ExplorationBackpackMenu(UUID ownerUuid) {
        super(ownerUuid, "§0§l任务背包", 54,0);
    }

    public boolean containSlot(int index) {
        return selectedSlots.contains(index);
    }

    public void selectSlot(int index) {
        if (selectedSlots.size() >= getBringSlotAmount()) return;
        selectedSlots.add(index);
    }

    public void unselectSlot(int index) {
        selectedSlots.remove(index);
    }

    /**
     * 允许的可带回物品最大数量
     */
    public int getBringSlotAmount() {
        boolean taskSuccess = playerTaskRecord.isSuccess();
        if(taskSuccess) {
            ExplorationDifficulty difficulty = IOC.getBean(DifficultyRepository.class)
                    .findByName(ExplorationDifficulty.class, playerTaskRecord.getTaskDifficulty());
            assert difficulty != null;
            return difficulty.getBringSlotAmount();
        } else {
            boolean safeWorking = IOC.getBean(TaskRelationProxy.class).getTaskProperties(playerTaskRecord.getTaskType())
                    .getOrnamentTypes().contains(OrnamentType.SAFE_WORKING);
            return safeWorking ? playerTaskRecord.getSafeSlotAmount() : 0;
        }
    }

    public int getSelectedSlotAmount() {
        return selectedSlots.size();
    }

    public void claimReward(Player player) {
        int emptySlots = 0;
        Inventory inv = player.getInventory();
        for (int i = 0;i < 36;i++) {
            ItemStack itemStack = inv.getItem(i);
            if(itemStack == null || itemStack.getType() == Material.AIR)emptySlots++;
        }
        if(emptySlots < getSelectedSlotAmount()) {
            player.playSound(player.getLocation(),Sound.ENTITY_VILLAGER_NO,1f,1f);
            Notifier.chat("你的背包没有足够的空间！",player);
            return;
        }
        // 背包格数检查
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1f);
        player.playSound(player.getLocation(), Sound.ENTITY_PIGLIN_CELEBRATE, 1f, 1f);
        player.sendTitle("领取成功", "任务中的物资已发放至背包", 8, 24, 8);
        playerTaskRecord.setClaimed(true);
        IOC.getBean(TaskRecordDB.class).saveRecord(playerTaskRecord);
        PlayerBackpack playerBackpack = playerTaskRecord.getPlayerBackpack();
        ExplorationDifficulty difficulty = IOC.getBean(DifficultyRepository.class)
                .findByName(ExplorationDifficulty.class, playerTaskRecord.getTaskDifficulty());
        assert difficulty != null;
        double rewardMultiple = difficulty.getRewardMultiple();
        double wheat = playerTaskRecord.getRewardWheat();
        int exp = (int) (playerBackpack.getTotalExp() * rewardMultiple);
        IOC.getBean(PlayerAPI.class).addExp(player,exp);
        String multiple = "§8(§7x"+String.format("%.0f",rewardMultiple * 100)+"%§8)";
        Notifier.chat("你从本次任务中收获了 §a" + String.format("%.0f", wheat) + " §6麦穗。"+multiple, player);
        Notifier.chat("你从本次任务中收获了 §a" + exp + " §e经验。"+multiple, player);
        Notifier.chat("你从本次任务中获得了 §a" + selectedSlots.size() + "格 §b物资。", player);
        IOC.getBean(VaultAPI.class).addEconomy(player, wheat);
        for (int index : selectedSlots) {
            if (index == 11) player.getInventory().addItem(playerBackpack.getHelmet());
            else if (index == 12) player.getInventory().addItem(playerBackpack.getChestplate());
            else if (index == 13) player.getInventory().addItem(playerBackpack.getLeggings());
            else if (index == 14) player.getInventory().addItem(playerBackpack.getBoots());
            else if (index == 15) player.getInventory().addItem(playerBackpack.getOffhand());
            else {
                ItemStack is = playerBackpack.getItems().get(index - 18);
                if (is == null)
                    Notifier.warn("玩家 " + player.getName() + " 在领取物资时，背包 第" + (index - 18) + "格 物品为空！");
                else player.getInventory().addItem(is);
            }
        }
        selectedSlots.clear();
        playerTaskRecord = null;
    }

    @Override
    protected void overrideIcons() {
        EmptyIcon emptyIcon = IOC.getBean(EmptyIcon.class);
        BorderIcon borderIcon = IOC.getBean(BorderIcon.class);
        // 清理默认边界图标
        Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 53, 52, 51, 50, 49, 48, 47, 46, 45)
                .forEach(index -> setIcon(index, emptyIcon));
        // 放置新的边界图标
        Stream.of(0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 16, 17).forEach(index -> setIcon(index, borderIcon));
        if (playerTaskRecord == null) return;
        setIcon(4, new ClaimTaskReward(this));
        PlayerBackpack playerBackpack = playerTaskRecord.getPlayerBackpack();
        if (playerBackpack == null) return;
        setIcon(11, new ExplorationBackpackItem(this, 11, playerBackpack.getHelmet()));
        setIcon(12, new ExplorationBackpackItem(this, 12, playerBackpack.getChestplate()));
        setIcon(13, new ExplorationBackpackItem(this, 13, playerBackpack.getLeggings()));
        setIcon(14, new ExplorationBackpackItem(this, 14, playerBackpack.getBoots()));
        setIcon(15, new ExplorationBackpackItem(this, 15, playerBackpack.getOffhand()));
        List<ItemStack> items = playerBackpack.getItems();
        if (items == null) return;
        int index = 18;
        for (ItemStack itemStack : items) {
            setIcon(index, new ExplorationBackpackItem(this, index, itemStack));
            index++;
        }
    }
}
