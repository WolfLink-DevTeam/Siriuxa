package org.wolflink.minecraft.plugin.siriuxa.task.tasks.exploration;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.api.Result;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.backpack.InvBackupService;
import org.wolflink.minecraft.plugin.siriuxa.backpack.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.Task;
import org.wolflink.minecraft.plugin.siriuxa.task.interfaces.ITaskService;
import org.wolflink.minecraft.plugin.siriuxa.task.regions.TaskArea;
import org.wolflink.minecraft.plugin.siriuxa.team.GlobalTeam;

import java.util.List;
import java.util.Random;

@Singleton
public class ExplorationTaskService implements ITaskService {

    private final Random random = new Random();
    @Inject
    private VaultAPI vaultAPI;
    @Inject
    private Config config;

    /**
     * 回到大厅(读取主背包后清理主背包数据)
     */
    @Override
    public void goLobby(Player player) {
        InvBackupService invBackupService = IOC.getBean(InvBackupService.class);
        invBackupService.applyInv(player, PlayerBackpack.getEmptyBackpack());
        Result r = invBackupService.applyMainInv(player);
        if(!r.result()) return;
        invBackupService.saveMainInv(player,PlayerBackpack.getEmptyBackpack());
        // 传送回城
        player.teleport(config.getLobbyLocation());
        if (!player.isOp()) player.setGameMode(GameMode.SURVIVAL);
    }

    /**
     * 前往任务地点
     */
    @Override
    public void goTask(Player player, Task task) {
        TaskArea taskArea = task.getTaskArea();
        if (taskArea == null) {
            Notifier.error("任务区域为空，玩家无法进入任务区域！");
            return;
        }
        InvBackupService invBackupService = IOC.getBean(InvBackupService.class);
        // 保存玩家背包信息
        invBackupService.saveMainInv(player);
        // 应用任务背包信息
        invBackupService.applyInv(player,task.getDefaultKit());
        // 发放5格背包物品
        invBackupService.giveFiveSlotBackpack(player);

        // 传送玩家到任务地点
        List<Location> spawnLocations = task.getSpawnLocations();
        if (spawnLocations.isEmpty()) player.teleport(task.getTaskArea().getCenter());
        else {
            Location location = spawnLocations.get(random.nextInt(spawnLocations.size())).clone().add(0.5, 1, 0.5);
            location.getBlock().setType(Material.AIR);
            location.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
            player.teleport(location);
        }
        // 设置玩家游戏模式
        if (!player.isOp()) player.setGameMode(GameMode.SURVIVAL);
    }

    @Override
    public Result create(Player player, Class<? extends Task> taskClass, TaskDifficulty taskDifficulty) {
        return null;
    }

    @Override
    public Result create(GlobalTeam globalTeam, Class<? extends Task> taskClass, TaskDifficulty taskDifficulty) {
        return null;
    }

    @Override
    public Result ready(Task task) {
        return null;
    }

    @Override
    public boolean canAccept(Class<? extends Task> taskClass,TaskDifficulty taskDifficulty, OfflinePlayer offlinePlayer) {
        if(taskClass != ExplorationTask.class) {
            Notifier.error("任务类型与任务业务类不匹配！");
            return false;
        }
        if(!(taskDifficulty instanceof ExplorationDifficulty explorationDifficulty)) {
            Notifier.error("任务难度与任务类型不匹配！");
            return false;
        }
        double cost = explorationDifficulty.getWheatCost();
        return (vaultAPI.getEconomy(offlinePlayer) >= cost);
    }

    @Override
    public void accept(Class<? extends Task> taskClass,TaskDifficulty taskDifficulty, OfflinePlayer offlinePlayer) {
        if(taskClass != ExplorationTask.class) {
            Notifier.error("任务类型与任务业务类不匹配！");
            return;
        }
        if(!(taskDifficulty instanceof ExplorationDifficulty explorationDifficulty)) {
            Notifier.error("任务难度与任务类型不匹配！");
            return;
        }
        vaultAPI.takeEconomy(offlinePlayer, explorationDifficulty.getWheatCost());
    }
}
