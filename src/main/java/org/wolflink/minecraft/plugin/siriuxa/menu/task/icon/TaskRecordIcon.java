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
import org.wolflink.minecraft.plugin.siriuxa.task.ornaments.OrnamentType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskProperties;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRelationProxy;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class TaskRecordIcon extends Icon {
    private final PlayerTaskRecord playerTaskRecord;
    private final boolean safeWorking;
    private final boolean suppliesCollection;

    public TaskRecordIcon(@NonNull PlayerTaskRecord playerTaskRecord) {
        super(0);
        this.playerTaskRecord = playerTaskRecord;
        Set<OrnamentType> ornamentTypes = IOC.getBean(TaskRelationProxy.class).getTaskProperties(playerTaskRecord.getTaskType()).getOrnamentTypes();
        safeWorking = ornamentTypes.contains(OrnamentType.SAFE_WORKING);
        suppliesCollection = ornamentTypes.contains(OrnamentType.SUPPLIES_COLLECTION);
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
        if (!playerTaskRecord.isSuccess()) {
            if(safeWorking) {
                claimStatus = playerTaskRecord.isClaimed() ? "§7补偿已领取" : "§a可领取补偿";
            } else claimStatus = "";
        }
        else {
            if(suppliesCollection) claimStatus = playerTaskRecord.isClaimed() ? "§7物资已领取" : "§a可领取物资";
            else claimStatus = "";
        }
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
        if (suppliesCollection && !playerTaskRecord.isClaimed()) {
            MenuService menuService = IOC.getBean(MenuService.class);
            ExplorationBackpackMenu menu = menuService.findMenu(player, ExplorationBackpackMenu.class);
            menu.setPlayerTaskRecord(playerTaskRecord);
            IOC.getBean(MenuService.class).display(menu, player);
        }
    }

    @Override
    public void rightClick(Player player) {
        // do nothing
    }
}
