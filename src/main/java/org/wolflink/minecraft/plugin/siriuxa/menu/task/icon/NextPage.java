package org.wolflink.minecraft.plugin.siriuxa.menu.task.icon;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Icon;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.TaskRecordMenu;

public class NextPage extends Icon {
    private final TaskRecordMenu taskRecordMenu;
    public NextPage(TaskRecordMenu taskRecordMenu) {
        super(false);
        this.taskRecordMenu = taskRecordMenu;
    }

    @Override
    protected @NonNull ItemStack createIcon() {
        return fastCreateItemStack(Material.ENDER_EYE,1,"下一页");
    }

    @Override
    public void leftClick(Player player) {
        if(taskRecordMenu.hasNextPage()) taskRecordMenu.setPage(taskRecordMenu.getPage()+1);
    }

    @Override
    public void rightClick(Player player) {
        // do nothing
    }
}
