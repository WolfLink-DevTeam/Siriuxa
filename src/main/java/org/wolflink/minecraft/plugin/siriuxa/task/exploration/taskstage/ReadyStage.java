package org.wolflink.minecraft.plugin.siriuxa.task.exploration.taskstage;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.stage.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.common.stage.TaskStage;

public class ReadyStage extends TaskStage {

    public ReadyStage(TaskLinearStageHolder stageHolder) {
        super("即将开始", stageHolder);
    }

    @Override
    protected void onEnter() {
        super.onEnter();
        Task task = getStageHolder().getTask();
        task.preLoad();
        Bukkit.getScheduler().runTaskAsynchronously(Siriuxa.getInstance(), () -> {
            int timeLeft = 15;
            while (timeLeft > 0) {
                for (Player player : task.getGlobalTeam().getPlayers()) {
                    player.sendTitle("§f§l" + timeLeft, "§7任务即将开始，请做好准备", 4, 12, 4);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1.2f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 1f, 1.2f);
                    Bukkit.getScheduler().runTask(Siriuxa.getInstance(), () -> {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 4, false, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10 * 20, 4, false, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 0, false, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 20, 2, false, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 10 * 20, 4, false, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 10 * 20, 4, false, false, false));
                    });
                }
                timeLeft--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                }
            }
            for (Player player : task.getGlobalTeam().getPlayers()) {
                player.sendTitle("§a§l任务开始", "§7作业单元已准备就绪，开始投放", 10, 40, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 0.5f);
                player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1f, 0.5f);
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 0.5f);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
            Bukkit.getScheduler().runTask(Siriuxa.getInstance(), () -> getStageHolder().next());
        });
    }
}
