package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.difficulty;

import lombok.NonNull;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.ItemIcon;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.DifficultyRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.MenuService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.TaskMenu;

public class DifficultyIcon extends ItemIcon {

    private final TaskDifficulty taskDifficulty;
    private final DifficultyRepository difficultyRepository;
    private final MenuService menuService;
    public DifficultyIcon(TaskDifficulty taskDifficulty) {
        super(false);
        this.taskDifficulty = taskDifficulty;
        this.menuService = IOC.getBean(MenuService.class);
        this.difficultyRepository = IOC.getBean(DifficultyRepository.class);
    }

    @Override
    public void leftClick(Player player) {
        menuService.selectDifficulty(player,taskDifficulty);
        player.closeInventory();
        player.sendTitle(taskDifficulty.color()+taskDifficulty.name(), "难度已选择",4,12,4);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE,1f,2f);
        SeriuxaJourney.getInstance().getSubScheduler().runTaskLater(()->{
            menuService.display(TaskMenu.class,player);
        },20);
    }

    @Override
    public void rightClick(Player player) {

    }

    @Override
    protected @NonNull ItemStack createIcon() {
        String levelStr = "✦".repeat(taskDifficulty.level()) +
                "✧".repeat(difficultyRepository.findAll().size() - taskDifficulty.level());
        return fastCreateItemStack(taskDifficulty.icon(),1,"§f难度 "+taskDifficulty.name(),
                " ",
                "§7风险指标 "+taskDifficulty.color()+levelStr,
                "§7任务成本 §f"+taskDifficulty.wheatCost()+" §6麦穗",
                "§7麦穗补助 §f"+taskDifficulty.wheatSupply()+" §6麦穗",
                "§7流速加快 §f+"+String.format("%.2f",taskDifficulty.wheatLostAcceleratedSpeed()*100)+"% §8/ §7每5分钟",
                "§7受伤惩罚 §f-"+String.format("%.2f",taskDifficulty.hurtWheatCost())+" §6麦穗 §8/ §71点伤害",
                "§7受伤倍率 §f+"+String.format("%.2f",taskDifficulty.hurtDamageMultiple()*100)+"§7%",
                "§7带回物资 §f"+taskDifficulty.bringSlotAmount()+"§7格",
                "§7麦穗转化 §f"+String.format("%.2f",taskDifficulty.wheatGainPercent()*100)+"§7%",
                "§7经验转化 §f"+String.format("%.2f",taskDifficulty.expGainPercent()*100)+"§7%",
                " "
                );
    }
}
