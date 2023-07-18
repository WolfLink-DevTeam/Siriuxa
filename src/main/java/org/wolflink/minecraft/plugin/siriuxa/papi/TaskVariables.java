package org.wolflink.minecraft.plugin.siriuxa.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRepository;

@Singleton
public class TaskVariables extends PlaceholderExpansion {
    @Inject
    private TaskRepository taskRepository;

    @Override
    public @NotNull String getIdentifier() {
        return "SJTask";
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
        Task task = taskRepository.findByPlayerUuid(offlinePlayer.getUniqueId());
        if (task == null) return "玩家未处于任务中";
        if (params.equalsIgnoreCase("wheat")) {
            return String.format("%.1f", task.getTaskWheat());
        }
        if (params.equalsIgnoreCase("wheat_loss_per_sec")) {
            return String.format("%.1f", task.getWheatLossPerSecNow());
        }
        if (params.equalsIgnoreCase("team_size")) {
            return String.valueOf(task.size());
        }
        if (params.equalsIgnoreCase("detail_wheat")) {
            double value = task.getTaskStat().getWheatChange();
            if (value > 0) return "§f%.1f§a(+%.1f)".formatted(task.getTaskWheat(), value);
            else if (value < 0) return "§f%.1f§c(%.1f)".formatted(task.getTaskWheat(), value);
            else return "§f%.1f".formatted(task.getTaskWheat());
        }
        if (params.equalsIgnoreCase("stage")) {
            return task.getStageHolder().getThisStage().getDisplayName();
        }
        if (params.equalsIgnoreCase("difficulty")) {
            return task.getTaskDifficulty().getColor() + task.getTaskDifficulty().getName();
        }
        if (params.equalsIgnoreCase("evacuable")) {
            if (task.getAvailableEvacuationZone() != null) return "§a可撤离";
            else return "§c无法撤离";
        }
        return "没做完呢";
    }
}
