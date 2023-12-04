package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.stage;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.BaseReadyStage;
import org.wolflink.minecraft.plugin.siriuxa.task.stages.TaskLinearStageHolder;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.interfaces.IPreloadable;

import java.util.function.Supplier;

public class ReadyStage extends BaseReadyStage {

    public ReadyStage(TaskLinearStageHolder stageHolder) {
        super(stageHolder);
    }
    private int timeLeft = 10;
    @Override
    protected @NonNull Supplier<Boolean> nextCondition() {
        boolean _isPreloadable = false;
        if(getStageHolder().getTask().getTaskLifeCycle() instanceof IPreloadable) {
            timeLeft = 30;
            _isPreloadable = true;
        }
        final boolean isPreloadable = _isPreloadable;
        return ()-> {
            boolean timeOut = (timeLeft == 0);
            if(!timeOut) return false;
            Task task = getStageHolder().getTask();
            if(isPreloadable) {
                boolean taskFinishPreload = ((IPreloadable) task.getTaskLifeCycle()).isPreloadFinished();
                if(!taskFinishPreload) {
                    task.getGlobalTeam().getPlayers().forEach(player ->
                            player.sendTitle(" ", "§e任务区域仍在加载中，已超出预计时间，请耐心等待...", 4, 12, 4)
                    );
                    return false;
                }
            }
            return true;
        };
    }
    @Override
    protected @NonNull Long nextCheckPeriodTick() {
        return 20L;
    }
    @Override
    protected void initScheduleTask() {
        getSubScheduler().runTaskTimerAsync(()->{
            Task task = getStageHolder().getTask();
            while (timeLeft > 0) {
                for (Player player : task.getGlobalTeam().getPlayers()) {
                    player.sendTitle("§f§l" + timeLeft, "§7任务区域生成中，可能会出现卡顿和滞后，请稍作休息", 4, 12, 4);
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
            }
        },20L,20L);
    }
    @Override
    protected void onEnter() {
        Task task = getStageHolder().getTask();
        if(task.getTaskLifeCycle() instanceof IPreloadable) getSubScheduler().runTaskAsync(((IPreloadable) task.getTaskLifeCycle())::preload);
    }

    @Override
    protected void onLeave() {

    }
}
