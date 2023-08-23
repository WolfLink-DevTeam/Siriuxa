package org.wolflink.minecraft.plugin.siriuxa.task.listeners.farmcheck;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.file.Lang;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration.taskstage.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.lumen.LumenTask;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

import java.util.List;

@Singleton
public class FarmChecker extends WolfirdListener {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private FarmValues farmValues;
    @Inject
    private Lang lang;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHarvest(BlockDropItemEvent e) {
        Player player = e.getPlayer();
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task == null) return; // 没有任务
        if (!(task instanceof LumenTask lumenTask)) return; // 不是麦穗任务，无法应用
        if (!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 任务没在游戏阶段
        if (task.getTaskArea() == null) return; // 任务区域未设定
        if (player.getWorld() != task.getTaskArea().getCenter().getWorld()) return; // 不在任务世界

        List<Item> dropItems = e.getItems();
        for (Item item : dropItems) {
            Material crop = item.getItemStack().getType();
            int amount = item.getItemStack().getAmount();
            if (farmValues.getCropTypes().contains(crop)) {
                if ((crop.equals(Material.CARROT) || crop.equals(Material.POTATO)) && amount <= 1) return;
                farmValues.doRecord(crop);
                double cropValue = farmValues.getCropValue(crop);
                lumenTask.addLumen(cropValue);
                for (Player taskPlayer : task.getTaskPlayers()) {
                    taskPlayer.playSound(taskPlayer.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_FALL, 1, 2f);
                    //TODO 改为 Hologram 提示
                    taskPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new TextComponent("§f" + player.getName() + " §7刚刚在 " + lang.get("crop." + crop.name().toLowerCase(), "未知作物") + " §7中发现 §f" + String.format("%.1f", cropValue) + " §7mg §d幽匿光体"));
                }
            }
        }
    }
}
