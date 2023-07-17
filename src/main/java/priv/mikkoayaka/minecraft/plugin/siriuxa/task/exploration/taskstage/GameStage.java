package priv.mikkoayaka.minecraft.plugin.siriuxa.task.exploration.taskstage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.siriuxa.api.world.RegionAPI;
import priv.mikkoayaka.minecraft.plugin.siriuxa.file.Config;
import priv.mikkoayaka.minecraft.plugin.siriuxa.file.ConfigProjection;
import priv.mikkoayaka.minecraft.plugin.siriuxa.invbackup.InvBackupService;
import priv.mikkoayaka.minecraft.plugin.siriuxa.invbackup.PlayerBackpack;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.region.SquareRegion;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.stage.TaskLinearStageHolder;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.stage.TaskStage;
import priv.mikkoayaka.minecraft.plugin.siriuxa.utils.Notifier;

public class GameStage extends TaskStage {
    private final Config config;
    private final Task task;

    public GameStage(TaskLinearStageHolder stageHolder) {
        super("正在进行", stageHolder);
        task = stageHolder.getTask();
        config = IOC.getBean(Config.class);
    }

    @Override
    protected void onEnter() {
        super.onEnter();
        String worldName = config.get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME);
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Notifier.error(worldName + "世界不存在！请检查配置文件");
            return;
        }
        InvBackupService invBackupService = IOC.getBean(InvBackupService.class);
        //缓存玩家背包
        for (Player player : task.getPlayers()) {
            invBackupService.saveMainInv(player);
            PlayerBackpack.getEmptyBackpack().apply(player);
        }
        Location regionCenter = IOC.getBean(RegionAPI.class).autoGetRegionCenter(world);
        getStageHolder().getTask().start(new SquareRegion(
                getStageHolder().getTask(),
                regionCenter
        ));
    }

    @Override
    protected void onLeave() {
        super.onLeave();
        //TODO 保存任务背包
    }
}
