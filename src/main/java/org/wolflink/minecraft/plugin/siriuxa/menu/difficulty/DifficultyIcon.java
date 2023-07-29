package org.wolflink.minecraft.plugin.siriuxa.menu.difficulty;

import lombok.NonNull;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Icon;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.DifficultyRepository;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.WheatTaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.menu.MenuService;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.TaskMenu;

public class DifficultyIcon<T extends WheatTaskDifficulty> extends Icon {

    private final T taskDifficulty;
    private final DifficultyRepository difficultyRepository;
    private final MenuService menuService;
    private final DifficultyMenu<T> difficultyMenu;

    public DifficultyIcon(DifficultyMenu<T> difficultyMenu, T taskDifficulty) {
        super(0);
        this.taskDifficulty = taskDifficulty;
        this.menuService = IOC.getBean(MenuService.class);
        this.difficultyRepository = IOC.getBean(DifficultyRepository.class);
        this.difficultyMenu = difficultyMenu;
    }

    @Override
    public void leftClick(Player player) {
        difficultyMenu.setSelectedDifficulty(taskDifficulty);
        player.closeInventory();
        player.sendTitle(taskDifficulty.getColor() + taskDifficulty.getName(), "难度已选择", 4, 12, 4);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 2f);
        Siriuxa.getInstance().getSubScheduler().runTaskLater(() -> menuService.display(TaskMenu.class, player), 20);
    }

    @Override
    public void rightClick(Player player) {
        // do nothing
    }

    @Override
    protected @NonNull ItemStack createIcon() {
        String levelStr = "✦".repeat(taskDifficulty.getLevel()) +
                "✧".repeat(difficultyRepository.findByType(ExplorationDifficulty.class).size() - taskDifficulty.getLevel());
        if (taskDifficulty instanceof ExplorationDifficulty explorationDifficulty) {
            return fastCreateItemStack(taskDifficulty.getIcon(), 1, "§f难度 " + taskDifficulty.getColor() + taskDifficulty.getName(),
                    " ",
                    "§7风险指标 " + taskDifficulty.getColor() + levelStr,
                    "§7任务成本 §f" + explorationDifficulty.getWheatCost() + " §6麦穗",
                    "§7麦穗补助 §f" + explorationDifficulty.getWheatSupply() + " §6麦穗",
                    "§7流速加快 §f+" + String.format("%.2f", explorationDifficulty.getWheatLostAcceleratedSpeed() * 100) + "% §8/ §7每5分钟",
                    "§7受伤惩罚 §f-" + String.format("%.2f", explorationDifficulty.getHurtWheatCost()) + " §6麦穗 §8/ §71点伤害",
                    "§7受伤倍率 §fx" + String.format("%.2f", explorationDifficulty.getHurtDamageMultiple() * 100) + "§7%",
                    "§7带回物资 §f" + explorationDifficulty.getBringSlotAmount() + "§7格",
                    "§7奖励倍率 §f" + String.format("%.0f", explorationDifficulty.getRewardMultiple() * 100) + "§7%",
                    " "
            );
        }
        return fastCreateItemStack(taskDifficulty.getIcon(), 1, "§f难度 " + taskDifficulty.getColor() + taskDifficulty.getName(),
                " ",
                "§7风险指标 " + taskDifficulty.getColor() + levelStr,
                " "
        );
    }
}
