package org.wolflink.minecraft.plugin.siriuxa.task.listeners.farmcheck;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.MetadataKey;
import org.wolflink.minecraft.plugin.siriuxa.file.Lang;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration.taskstage.GameStage;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.lumen.LumenTask;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class FarmChecker extends WolfirdListener {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private FarmValues farmValues;
    @Inject
    private Lang lang;

    private final Set<Material> availableAgeable = Stream.of(Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS).collect(Collectors.toSet());

    /**
     * 此处只监听 小麦/马铃薯/胡萝卜/甜菜根 四种作物
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCropHarvest(BlockDropItemEvent e) {
        Player player = e.getPlayer();
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task == null) return; // 没有任务
        if (!(task instanceof LumenTask lumenTask)) return; // 不是麦穗任务，无法应用
        if (!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 任务没在游戏阶段
        if (task.getTaskArea() == null) return; // 任务区域未设定
        if (player.getWorld() != task.getTaskArea().getCenter().getWorld()) return; // 不在任务世界
        if (e.getBlockState().getBlockData() instanceof Ageable ageable) { // 是可成长的
            if (availableAgeable.contains(ageable.getMaterial())) {
                if (ageable.getAge() == ageable.getMaximumAge()) { // 达到最大年龄
                    recordAndAddLumen(lumenTask, ageable.getMaterial());
                }
            }
        }
    }

    private final Set<Material> availableHarvestBlocks = Stream.of(Material.PUMPKIN, Material.MELON).collect(Collectors.toSet());

    @EventHandler
    public void onHarvestBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task == null) return; // 没有任务
        if (!(task instanceof LumenTask lumenTask)) return; // 不是麦穗任务，无法应用
        if (!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 任务没在游戏阶段
        if (task.getTaskArea() == null) return; // 任务区域未设定
        if (player.getWorld() != task.getTaskArea().getCenter().getWorld()) return; // 不在任务世界
        if (availableHarvestBlocks.contains(e.getBlock().getType())) {
            e.getBlock().setMetadata(MetadataKey.HARVEST_BLOCK_PLACER.getKey(), new FixedMetadataValue(Siriuxa.getInstance(), e.getPlayer().getName()));
        }
    }

    @EventHandler
    public void onBlockHarvest(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task == null) return; // 没有任务
        if (!(task instanceof LumenTask lumenTask)) return; // 不是麦穗任务，无法应用
        if (!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 任务没在游戏阶段
        if (task.getTaskArea() == null) return; // 任务区域未设定
        if (player.getWorld() != task.getTaskArea().getCenter().getWorld()) return; // 不在任务世界
        if (availableHarvestBlocks.contains(e.getBlock().getType())) {
            if (e.getBlock().hasMetadata(MetadataKey.HARVEST_BLOCK_PLACER.getKey())) return;
            recordAndAddLumen(lumenTask, e.getBlock().getType());
        }
    }

    private void recordAndAddLumen(LumenTask lumenTask, Material material) {
        farmValues.doRecord(material);
        double cropValue = farmValues.getCropValue(material);
        lumenTask.addLumen(cropValue);
        for (Player taskPlayer : lumenTask.getTaskPlayers()) {
            taskPlayer.playSound(taskPlayer.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_FALL, 1, 2f);
            //TODO 改为 Hologram 提示
            taskPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent("§f" + taskPlayer.getName() + " §7刚刚在 " + lang.get("crop." + material.name().toLowerCase(), "未知作物") + " §7中发现 §f" + String.format("%.1f", cropValue) + " §7mg §d幽匿光体"));
        }
    }
}
