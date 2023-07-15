package priv.mikkoayaka.minecraft.plugin.seriuxajourney.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.TaskRepository;

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
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params){
        if(offlinePlayer == null)return "不存在的玩家";
        Task task = taskRepository.findByUuid(offlinePlayer.getUniqueId());
        if(task == null) return "玩家未处于任务中";
        if(params.equalsIgnoreCase("wheat")) {
            return String.format("%.2f",task.getTaskWheat());
        }
        if(params.equalsIgnoreCase("wheat_loss_per_sec")) {
            return String.format("%.2f",task.getWheatLossPerSecNow());
        }
        if(params.equalsIgnoreCase("team_size")) {
            return String.valueOf(task.getTaskTeam().size());
        }
        return "没做完呢";
    }
}
