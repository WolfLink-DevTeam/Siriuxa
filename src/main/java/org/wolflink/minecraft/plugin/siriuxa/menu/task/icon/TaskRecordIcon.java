package org.wolflink.minecraft.plugin.siriuxa.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.PlayerAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Icon;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.DifficultyRepository;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.WheatTaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerTaskRecord;
import org.wolflink.minecraft.plugin.siriuxa.file.database.TaskRecordDB;
import org.wolflink.minecraft.plugin.siriuxa.menu.MenuService;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.ExplorationBackpackMenu;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskRecordIcon extends Icon {
    private final PlayerTaskRecord playerTaskRecord;

    public TaskRecordIcon(@NonNull PlayerTaskRecord playerTaskRecord) {
        super(0);
        this.playerTaskRecord = playerTaskRecord;
    }

    @Override
    protected @NonNull ItemStack createIcon() {
        Date taskFinishedDate = new Date(playerTaskRecord.getFinishedTimeInMills());
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.CHINA);
        String date = dateFormat.format(taskFinishedDate);
        String iconName = "§8[ §f任务记录 §8] §7%s";
        iconName = String.format(iconName, date);
        String taskResult = playerTaskRecord.isSuccess() ? "§a完成" : "§c失败";
        int minutes = (int) (playerTaskRecord.getUsingTimeInMills() / 60000);
        String claimStatus;
        if (!playerTaskRecord.isSuccess()) claimStatus = playerTaskRecord.isClaimed() ? "§7补偿已领取" : "§a可领取补偿";
        else claimStatus = playerTaskRecord.isClaimed() ? "§7物资已领取" : "§a可领取物资";
        return fastCreateItemStack(Material.PAPER, 1, iconName,
                " ",
                "  §7完成时间 §f" + timeFormat.format(taskFinishedDate),
                " ",
                "  §7任务类型 §f" + playerTaskRecord.getTaskType(),
                "  §7任务难度 §f" + playerTaskRecord.getTaskDifficulty(),
                "  §7任务结果 §r" + taskResult,
                "  §7任务用时 §f" + minutes + "分钟",
                " ",
                "  " + claimStatus,
                " "
        );
    }

    @Override
    public void leftClick(Player player) {
        if (!playerTaskRecord.isClaimed()) {
            if (playerTaskRecord.isSuccess()) {
                MenuService menuService = IOC.getBean(MenuService.class);
                ExplorationBackpackMenu menu = menuService.findMenu(player, ExplorationBackpackMenu.class);
                menu.setPlayerTaskRecord(playerTaskRecord);
                IOC.getBean(MenuService.class).display(menu, player);
            } else {
                playerTaskRecord.setClaimed(true);
                IOC.getBean(TaskRecordDB.class).saveRecord(playerTaskRecord);
                WheatTaskDifficulty wheatTaskDifficulty = IOC.getBean(DifficultyRepository.class).findByName(ExplorationDifficulty.class, playerTaskRecord.getTaskDifficulty());
                if(wheatTaskDifficulty == null) {
                    Notifier.error("尝试给玩家"+player.getName()+"发放奖励时，未找到对应的难度类："+ playerTaskRecord.getTaskDifficulty());
                    return;
                }
                double rewardWheat = playerTaskRecord.getRewardWheat();
                int exp = (playerTaskRecord.getPlayerBackpack().getTotalExp());
                IOC.getBean(VaultAPI.class).addEconomy(player, rewardWheat);
                Notifier.chat("任务失败，但你仍然获得了 §a"+String.format("%.0f",rewardWheat)+" §6麦穗 §f以及 §a"+exp+" §e经验 §f作为奖励。",player);
                IOC.getBean(PlayerAPI.class).addExp(player,exp);
                Notifier.chat("任务奖励的麦穗与经验已发放，但物品丢失了，祝你下次好运！", player);
                player.playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 0.7f, 1f);
            }
        }
    }

    @Override
    public void rightClick(Player player) {
        // do nothing
    }
}
