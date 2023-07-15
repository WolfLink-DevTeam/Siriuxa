package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.stage.TaskLinearStageHolder;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.stage.TaskStage;

public class ReadyStage extends TaskStage {

    public ReadyStage(TaskLinearStageHolder stageHolder) {
        super("即将开始", stageHolder);
    }

    @Override
    protected void onEnter() {
        Task task = getStageHolder().getTask();
        Bukkit.getScheduler().runTaskAsynchronously(SeriuxaJourney.getInstance(),()->{
            int timeLeft = 15;
            while (timeLeft > 0) {
                for (Player player : task.getPlayers()) {
                    player.sendTitle("§f§l"+timeLeft,"§7任务即将开始，请做好准备",4,12,4);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME,1f,1.2f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR,1f,1.2f);
                }
                timeLeft--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            for (Player player : task.getPlayers()) {
                player.sendTitle("§a§l任务开始","§7作业单元已准备就绪，开始投放",10,40,10);
                player.playSound(player.getLocation(),Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,1f,0.5f);
                player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,1f,0.5f);
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1f,0.5f);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Bukkit.getScheduler().runTask(SeriuxaJourney.getInstance(),()->getStageHolder().next());
        });

    }
}
