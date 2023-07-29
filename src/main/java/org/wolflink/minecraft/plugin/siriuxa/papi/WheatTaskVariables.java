package org.wolflink.minecraft.plugin.siriuxa.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.exploration.WheatTask;

@Singleton
public class WheatTaskVariables extends PlaceholderExpansion {
    @Inject
    private TaskRepository taskRepository;

    @Override
    public @NotNull String getIdentifier() {
        return "WheatTask";
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
        WheatTask wheatTask = (WheatTask) taskRepository.findByTaskTeamPlayerUuid(offlinePlayer.getUniqueId());
        if (wheatTask == null) return "暂无任务";
        switch (params.toLowerCase()) {
            case "wheat" -> {
                return String.format("%.1f", wheatTask.getTaskWheat());
            }
            case "wheat_loss_per_sec" -> {
                return String.format("%.1f", wheatTask.getWheatLossPerSecNow());
            }
            case "team_size" -> {
                return wheatTask.getTaskTeamSize() + "人";
            }
            case "detail_wheat" -> {
                double value = wheatTask.getTaskStat().getWheatChange();
                if (value > 0) return "§f%.1f§a(+%.1f)".formatted(wheatTask.getTaskWheat(), value);
                else if (value < 0) return "§f%.1f§c(%.1f)".formatted(wheatTask.getTaskWheat(), value);
                else return "§f%.1f".formatted(wheatTask.getTaskWheat());
            }
            case "stage" -> {
                return wheatTask.getStageHolder().getThisStage().getDisplayName();
            }
            case "difficulty" -> {
                return wheatTask.getTaskDifficulty().getColor() + wheatTask.getTaskDifficulty().getName();
            }
            case "evacuable" -> {
                if (wheatTask.getAvailableEvacuationZone() != null) return "§a可撤离";
                else return "§c无法撤离";
            }
            default -> {
                return "没做完呢";
            }
        }
    }
}
