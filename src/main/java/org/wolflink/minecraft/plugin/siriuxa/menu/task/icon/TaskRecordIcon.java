package org.wolflink.minecraft.plugin.siriuxa.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Icon;
import org.wolflink.minecraft.plugin.siriuxa.file.database.PlayerTaskRecord;
import org.wolflink.minecraft.plugin.siriuxa.menu.MenuService;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.ExplorationBackpackMenu;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskRecordIcon extends Icon {
    private final PlayerTaskRecord playerTaskRecord;
    public TaskRecordIcon(@NonNull PlayerTaskRecord playerTaskRecord) {
        super(false);
        this.playerTaskRecord = playerTaskRecord;
    }

    @Override
    protected @NonNull ItemStack createIcon() {
        Date taskFinishedDate = new Date(playerTaskRecord.getFinishedTimeInMills());
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.CHINA);
        String date = dateFormat.format(taskFinishedDate);
        String iconName = "§8[ §f任务记录 §8] §7%s";
        iconName = String.format(iconName,date);
        String taskResult = playerTaskRecord.isSuccess() ? "§a完成" : "§c失败";
        int minutes = (int) (playerTaskRecord.getUsingTimeInMills() / 60000);
        String claimStatus = (playerTaskRecord.isClaimed()&&playerTaskRecord.isSuccess()) ? "§7物资已领取" : "§a可领取物资";
        return fastCreateItemStack(Material.PAPER,1,iconName,
                " ",
                "  §7完成时间 §f"+timeFormat.format(taskFinishedDate),
                " ",
                "  §7任务类型 §f"+playerTaskRecord.getTaskType(),
                "  §7任务难度 §f"+playerTaskRecord.getTaskDifficulty(),
                "  §7任务结果 §r"+taskResult,
                "  §7任务用时 §f"+minutes+"分钟",
                " ",
                "  "+claimStatus,
                " "
                );
    }

    @Override
    public void leftClick(Player player) {
        if(!playerTaskRecord.isClaimed() && playerTaskRecord.isSuccess()) {
            MenuService menuService = IOC.getBean(MenuService.class);
            ExplorationBackpackMenu menu = menuService.findMenu(player, ExplorationBackpackMenu.class);
            menu.setPlayerTaskRecord(playerTaskRecord);
            IOC.getBean(MenuService.class).display(menu,player);
        }
    }

    @Override
    public void rightClick(Player player) {

    }
}
