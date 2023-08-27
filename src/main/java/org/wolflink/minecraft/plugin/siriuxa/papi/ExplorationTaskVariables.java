package org.wolflink.minecraft.plugin.siriuxa.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration.ExplorationTask;

@Singleton
public class ExplorationTaskVariables extends PlaceholderExpansion {
    @Inject
    private TaskRepository taskRepository;

    @Override
    public @NotNull String getIdentifier() {
        return "ExplorationTask";
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
        if (!(task instanceof ExplorationTask explorationTask)) return "暂无该类型任务";
        switch (params.toLowerCase()) {
            case "lumen" -> {
                return String.format("%.1f", explorationTask.getTaskLumen());
            }
            case "lumen_loss_per_sec" -> {
                return String.format("%.1f", explorationTask.getLumenLossPerSecNow());
            }
            case "team_size" -> {
                return explorationTask.getTaskTeamSize() + "人";
            }
            case "detail_lumen" -> {
                double value = explorationTask.getTaskStat().getWheatChange();
                if (value > 0) return "§f%.1f§a(+%.1f)".formatted(explorationTask.getTaskLumen(), value);
                else if (value < 0) return "§f%.1f§c(%.1f)".formatted(explorationTask.getTaskLumen(), value);
                else return "§f%.1f".formatted(explorationTask.getTaskLumen());
            }
            case "stages" -> {
                return explorationTask.getStageHolder().getThisStage().getDisplayName();
            }
            case "difficulty" -> {
                return explorationTask.getTaskDifficulty().getColor() + explorationTask.getTaskDifficulty().getName();
            }
            case "evacuable" -> {
                if (explorationTask.getAvailableEvacuationZone() != null) return "§a可撤离";
                else return "§c等待中";
            }
            case "eva_loc" -> {
                if (explorationTask.getAvailableEvacuationZone() != null)
                    return "§7[ §a"
                            .concat(String.valueOf(explorationTask.getAvailableEvacuationZone().getCenter().getBlockX()))
                            .concat("§8, §a")
                            .concat(String.valueOf(explorationTask.getAvailableEvacuationZone().getCenter().getBlockY()))
                            .concat("§8, §a")
                            .concat(String.valueOf(explorationTask.getAvailableEvacuationZone().getCenter().getBlockZ()))
                            .concat(" §7]");
                else return "§7[ ?, ?, ? ]";
            }
            case "lumen_time" -> {
                return "§a"+explorationTask.getTaskStat().getLumenTimeLeft()+" §f秒";
            }
            default -> {
                return "没做完呢";
            }
        }
    }
}
