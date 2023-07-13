package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import org.wolflink.minecraft.wolfird.framework.notifier.BaseNotifier;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.Result;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.VaultAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Config;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.ConfigProjection;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.region.SquareRegion;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.taskstage.ReadyStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.taskstage.WaitStage;

@Singleton
public class ExplorationService {

    @Inject
    private ExplorationTaskRepository explorationTaskRepository;
    @Inject
    private VaultAPI vaultAPI;
    @Inject
    private Config config;
    private final BaseNotifier notifier = SeriuxaJourney.getInstance().getNotifier();

    public Result createTask(Player player,TaskDifficulty taskDifficulty) {
        ExplorationTask task = new ExplorationTask(taskDifficulty);
        Result joinResult = joinTask(player,task);
        if(!joinResult.result())return joinResult;
        explorationTaskRepository.insert(task);
        return new Result(true,"任务登记完成。");
    }
    public Result startTask(ExplorationTask explorationTask) {
        //TODO X Z 动态分配
        //TODO 玩家背包隔离
        if(explorationTask.getPlayers().size() == 0) {
            explorationTaskRepository.deleteByKey(explorationTask.getTaskId());
            return new Result(false,"该任务没有任何在线玩家。");
        }
        explorationTask.start(new SquareRegion(
                explorationTask,
                config.get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME),
                0,
                0,
                500
        ));
        return new Result(true,"任务开始。");
    }

    /**
     * 尝试加入任务
     *
     * 检查任务是否可加入
     * 检查玩家是否在其他任务中
     * 检查玩家是否有足够的麦穗
     */
    public Result joinTask(Player player,ExplorationTask explorationTask) {
        Stage stage = explorationTask.getLinearStageHolder().getThisStage();
        if(!(stage instanceof WaitStage)) {
            return new Result(false,"当前任务状态为："+stage.getDisplayName()+"，不允许加入。");
        }
        if(explorationTaskRepository.findByPlayer(player) != null) {
            return new Result(false,"玩家当前已处在其他任务中，不允许加入。");
        }
        int wheatCost = explorationTask.getDifficulty().wheatCost();
        int wheatSupply = explorationTask.getDifficulty().wheatSupply();
        EconomyResponse r = vaultAPI.getEconomy().withdrawPlayer(player,wheatCost);
        if(r.transactionSuccess()) {
            explorationTask.getPlayerUuids().add(player.getUniqueId());
            explorationTask.setTaskWheat(explorationTask.getTaskWheat() + wheatCost + wheatSupply);
            return new Result(true,"加入成功");
        }
        return new Result(false,"你需要支付 "+wheatCost+" 才能加入这次任务，显然你还没有足够的麦穗。");
    }

    /**
     * 离开玩家当前所处的任务
     *
     * 如果处于准备阶段，则退回玩家麦穗
     * 否则不会退回
     */
    public Result leaveTask(Player player) {
        ExplorationTask task = explorationTaskRepository.findByPlayer(player);
        if(task == null) return new Result(false,"你没有处在任何任务当中。");
        task.getPlayerUuids().remove(player.getUniqueId());
        // 清理掉没有玩家的任务
        if(task.getPlayerUuids().size() == 0)explorationTaskRepository.deleteByValue(task);
        Stage stage = task.getLinearStageHolder().getThisStage();
        int wheatCost = task.getDifficulty().wheatCost();
        int wheatSupply = task.getDifficulty().wheatSupply();
        // 退回麦穗
        if(stage instanceof WaitStage || stage instanceof ReadyStage) {
            // 扣除该玩家提供的全部麦穗
            task.takeWheat(wheatCost+wheatSupply);
            EconomyResponse r = vaultAPI.getEconomy().depositPlayer(player,wheatCost);
            if(!(r.transactionSuccess())) {
                notifier.warn("在尝试退回玩家"+player.getName()+"麦穗时出现了问题。");
            }
        } else {
            // 任务中扣除其一半麦穗
            task.takeWheat((wheatCost + wheatSupply) * 0.5,"玩家 "+player.getName()+" 中途退出了，但他仍保留下来一部分麦穗。");
        }
        Location location = config.getLobbyLocation();
        if(location != null) player.teleport(location);
        // TODO 涉及到背包和等级隔离的问题
        return new Result(true,"任务退出成功");
    }
}
