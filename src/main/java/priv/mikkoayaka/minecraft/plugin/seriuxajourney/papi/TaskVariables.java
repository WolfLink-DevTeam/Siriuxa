package priv.mikkoayaka.minecraft.plugin.seriuxajourney.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationTask;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.ExplorationTaskRepository;

@Singleton
public class TaskVariables extends PlaceholderExpansion {
    @Inject
    private ExplorationTaskRepository explorationTaskRepository;
    @Override
    public @NotNull String getIdentifier() {
        return "SeriuxaJourney_Task";
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
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params){
        if(offlinePlayer == null)return "不存在的玩家";
        ExplorationTask explorationTask = explorationTaskRepository.findByUuid(offlinePlayer.getUniqueId());
        if(explorationTask == null) return "玩家未处于任务中";
        if(params.equalsIgnoreCase("wheat")) {
            return String.format("%.2f",explorationTask.getTaskWheat());
        }
        if(params.equalsIgnoreCase("wheat_loss_per_sec")) {
            return String.format("%.2f",explorationTask.getWheatLossPerSecNow());
        }
        if(params.equalsIgnoreCase("team_size")) {
            return String.valueOf(explorationTask.getPlayerUuids().size());
        }
        return "没做完呢";
    }
}
