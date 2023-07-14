package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.difficulty;

import lombok.NonNull;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.ItemIcon;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.DifficultyRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.ExplorationDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.MenuService;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.TaskMenu;

public class DifficultyIcon extends ItemIcon {

    private final ExplorationDifficulty explorationDifficulty;
    private final DifficultyRepository difficultyRepository;
    private final MenuService menuService;
    public DifficultyIcon(ExplorationDifficulty explorationDifficulty) {
        super(false);
        this.explorationDifficulty = explorationDifficulty;
        this.menuService = IOC.getBean(MenuService.class);
        this.difficultyRepository = IOC.getBean(DifficultyRepository.class);
    }

    @Override
    public void leftClick(Player player) {
        menuService.selectDifficulty(player, explorationDifficulty);
        player.closeInventory();
        player.sendTitle(explorationDifficulty.getColor()+ explorationDifficulty.getName(), "难度已选择",4,12,4);
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
        String levelStr = "✦".repeat(explorationDifficulty.getLevel()) +
                "✧".repeat(difficultyRepository.findByType(ExplorationDifficulty.class).size() - explorationDifficulty.getLevel());
        return fastCreateItemStack(explorationDifficulty.getIcon(),1,"§f难度 "+ explorationDifficulty.getColor()+ explorationDifficulty.getName(),
                " ",
                "§7风险指标 "+ explorationDifficulty.getColor()+levelStr,
                "§7任务成本 §f"+ explorationDifficulty.getWheatCost()+" §6麦穗",
                "§7麦穗补助 §f"+ explorationDifficulty.getWheatSupply()+" §6麦穗",
                "§7流速加快 §f+"+String.format("%.2f", explorationDifficulty.getWheatLostAcceleratedSpeed()*100)+"% §8/ §7每5分钟",
                "§7受伤惩罚 §f-"+String.format("%.2f", explorationDifficulty.getHurtWheatCost())+" §6麦穗 §8/ §71点伤害",
                "§7受伤倍率 §fx"+String.format("%.2f", explorationDifficulty.getHurtDamageMultiple()*100)+"§7%",
                "§7带回物资 §f"+ explorationDifficulty.getBringSlotAmount()+"§7格",
                "§7麦穗转化 §f"+String.format("%.2f", explorationDifficulty.getWheatGainPercent()*100)+"§7%",
                "§7经验转化 §f"+String.format("%.2f", explorationDifficulty.getExpGainPercent()*100)+"§7%",
                " "
                );
    }
}
