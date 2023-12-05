package org.wolflink.minecraft.plugin.siriuxa.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.abstracts.TaskAttributeType;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.ComposableTask;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.timetrap.TaskLumenPool;

@Singleton
public class ComposableTaskVariables extends PlaceholderExpansion {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private TaskLumenPool lumenPool;

    @Override
    public @NotNull String getIdentifier() {
        return "ComposableTask";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MikkoAyaka";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null) return "不存在的玩家";
        Task task = taskRepository.findByTaskTeamPlayerUuid(offlinePlayer.getUniqueId());
        if (task == null) return "暂无任务";
        if (!(task instanceof ComposableTask composableTask)) return "暂无该类型任务";
        switch (params.toLowerCase()) {
            case "lumen" -> {
                return String.format("%.1f", lumenPool.getTaskLumen(composableTask));
            }
            case "lumen_loss_per_sec" -> {
                return String.format("%.1f", lumenPool.getTaskLumenLostPerSecondNow(composableTask));
            }
            case "team_size" -> {
                return composableTask.getTaskTeamSize() + "人";
            }
            case "detail_lumen" -> {
                double value = composableTask.getTaskAttribute().getAttribute(TaskAttributeType.LUMEN_LEFT_DELTA,0.0);
                if (value > 0) return "§f%.1f§a(+%.1f)".formatted(lumenPool.getTaskLumen(composableTask), value);
                else if (value < 0) return "§f%.1f§c(%.1f)".formatted(lumenPool.getTaskLumen(composableTask), value);
                else return "§f%.1f".formatted(lumenPool.getTaskLumen(composableTask));
            }
            case "stages" -> {
                return composableTask.getStageHolder().getThisStage().getDisplayName();
            }
            case "difficulty" -> {
                return composableTask.getTaskDifficulty().getColor() + composableTask.getTaskDifficulty().getName();
            }
            case "evacuable" -> {
                if (composableTask.getTaskRegion().getEvacuationZone() != null) return "§a可撤离";
                else return "§c等待中";
            }
            case "eva_loc" -> {
                if (composableTask.getTaskRegion().getEvacuationZone() != null)
                    return "§7[ §a"
                            .concat(String.valueOf(composableTask.getTaskRegion().getEvacuationZone().getCenter().getBlockX()))
                            .concat("§8, §a")
                            .concat(String.valueOf(composableTask.getTaskRegion().getEvacuationZone().getCenter().getBlockY()))
                            .concat("§8, §a")
                            .concat(String.valueOf(composableTask.getTaskRegion().getEvacuationZone().getCenter().getBlockZ()))
                            .concat(" §7]");
                else return "§7[ ?, ?, ? ]";
            }
            case "lumen_time" -> {
                return "§a" + lumenPool.getLumenTimeLeft(composableTask) + " §f秒";
            }
            default -> {
                return "没做完呢";
            }
        }
    }
}
