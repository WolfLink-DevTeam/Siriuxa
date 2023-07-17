package org.wolflink.minecraft.plugin.siriuxa.task.exploration;

import org.bukkit.OfflinePlayer;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.task.common.TaskRepository;
import org.wolflink.minecraft.plugin.siriuxa.team.TaskTeam;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;

import java.util.List;

@Singleton
public class ExplorationService {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private VaultAPI vaultAPI;

    public Result create(TaskTeam taskTeam, ExplorationDifficulty explorationDifficulty) {
        if (taskTeam.getSelectedTask() != null) return new Result(false, "当前队伍已经选择了任务，无法再次创建。");
        double cost = explorationDifficulty.getWheatCost();
        List<OfflinePlayer> offlinePlayers = taskTeam.getOfflinePlayers();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (!offlinePlayer.isOnline()) return new Result(false, "队伍中有离线玩家，无法开始任务。");
        }
        // 检查成员余额
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (vaultAPI.getEconomy(offlinePlayer) < cost)
                return new Result(false, "队伍中至少有一名成员无法支付本次任务费用。");
        }
        // 成员支付任务成本
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            vaultAPI.takeEconomy(offlinePlayer, cost);
        }
        // 创建任务
        ExplorationTask task = new ExplorationTask(taskTeam, explorationDifficulty);
        // 与队伍绑定
        taskTeam.setSelectedTask(task);
        taskRepository.insert(task);
        return new Result(true, "任务登记完成。");
    }
}
