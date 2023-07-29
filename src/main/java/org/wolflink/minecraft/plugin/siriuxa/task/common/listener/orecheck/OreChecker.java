package org.wolflink.minecraft.plugin.siriuxa.task.common.listener.orecheck;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.file.Lang;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.WheatTask;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage.GameStage;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;

@Singleton
public class OreChecker extends WolfirdListener {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private OreValues oreValues;
    @Inject
    private Lang lang;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void oreBlockCheck(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        Location checkLoc = player.getLocation().add(0, -1, 0);
        Task task = taskRepository.findByTaskTeamPlayer(player);
        if (task == null) return; // 没有任务
        if (!(task instanceof WheatTask wheatTask)) return; // 任务模式不可用该检测
        if (!(task.getStageHolder().getThisStage() instanceof GameStage)) return; // 任务没在游戏阶段
        if (task.getTaskRegion() == null) return; // 任务区域未设定
        if (checkLoc.getWorld() != task.getTaskRegion().getCenter().getWorld()) return; // 不在任务世界
        Block block = checkLoc.getBlock();
        Material material = block.getType();
        if (!oreValues.getOreMaterials().contains(material)) return;
        oreValues.doRecord(material);
        block.setType(Material.AIR);
        double wheatValue = oreValues.getOreValue(material);
        wheatTask.addWheat(wheatValue);
        for (Player taskPlayer : task.getTaskPlayers()) {
            taskPlayer.playSound(taskPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1.5f);
            //TODO 改为 Hologram 提示
            taskPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent("§f" + player.getName() + " §7刚刚出售了 " + lang.get("material." + material.name().toLowerCase(), "未知方块") + " §7换取 §f" + String.format("%.1f", wheatValue) + " §6麦穗"));
        }
        renderBlockBorder(block.getLocation());
    }

    private void renderBlockBorder(Location center) {
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {
            World world = center.getWorld();
            for (double x = 0; x <= 1; x += 0.1) {
                world.spawnParticle(Particle.END_ROD, center.clone().add(x, 0, 0), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD, center.clone().add(x, 0, 1), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD, center.clone().add(x, 1, 0), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD, center.clone().add(x, 1, 1), 1, 0, 0, 0, 0);
            }
            for (double y = 0; y <= 1; y += 0.1) {
                world.spawnParticle(Particle.END_ROD, center.clone().add(0, y, 0), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD, center.clone().add(0, y, 1), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD, center.clone().add(1, y, 0), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD, center.clone().add(1, y, 1), 1, 0, 0, 0, 0);
            }
            for (double z = 0; z <= 1; z += 0.1) {
                world.spawnParticle(Particle.END_ROD, center.clone().add(0, 0, z), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD, center.clone().add(0, 1, z), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD, center.clone().add(1, 0, z), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD, center.clone().add(1, 1, z), 1, 0, 0, 0, 0);
            }
        });
    }
}
