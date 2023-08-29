package org.wolflink.minecraft.plugin.siriuxa.task.ornaments.safeworking;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.backpack.InvBackupService;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskEndEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.events.TaskStartEvent;
import org.wolflink.minecraft.plugin.siriuxa.task.ornaments.OrnamentType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class SafeWorkingListener extends WolfirdListener {
    @Inject
    private InvBackupService service;

    @EventHandler
    void on(TaskStartEvent event) {
        Task task = event.getTask();
        if (!task.getOrnamentTypes().contains(OrnamentType.SAFE_WORKING)) return;
        // 发放5格背包物品
        for (Player player : task.getTaskPlayers()) {
            service.giveFiveSlotBackpack(player);
        }
    }

    @EventHandler
    void on(TaskEndEvent event) {
        Task task = event.getTask();
        if (!task.getOrnamentTypes().contains(OrnamentType.SAFE_WORKING)) return;
        for (OfflinePlayer offlinePlayer : task.getGlobalTeam().getOfflinePlayers()) {
            service.clearFiveSlotBackpack(offlinePlayer);
        }
    }
}
