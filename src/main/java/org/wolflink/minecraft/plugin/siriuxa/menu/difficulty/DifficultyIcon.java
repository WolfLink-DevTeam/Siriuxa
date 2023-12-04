package org.wolflink.minecraft.plugin.siriuxa.menu.difficulty;

import lombok.NonNull;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Icon;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.DifficultyRepository;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.menu.MenuService;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.TaskMenu;

public class DifficultyIcon extends Icon {

    private final TaskDifficulty taskDifficulty;
    private final DifficultyRepository difficultyRepository;
    private final MenuService menuService;
    private final DifficultyMenu difficultyMenu;

    public DifficultyIcon(DifficultyMenu difficultyMenu, TaskDifficulty taskDifficulty) {
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
                "✧".repeat(difficultyRepository.findAll().size() - taskDifficulty.getLevel());
        // TODO 补充任务与难度相关的额外信息
        return fastCreateItemStack(taskDifficulty.getIcon(), 1, "§f难度 " + taskDifficulty.getColor() + taskDifficulty.getName(),
                " ",
                "§7风险指标 " + taskDifficulty.getColor() + levelStr,
                "§e§l暂无难度额外信息，开发中",
//                "§7任务成本 §f" + explorationDifficulty.getLumenCost() + " §6麦穗",
//                "§7初始光体 §f" + explorationDifficulty.getLumenSupply() + " §d幽匿光体",
//                "§7流速加快 §f+" + String.format("%.2f", explorationDifficulty.getLumenLostAcceleratedSpeed() * 100) + "% §8/ §7每5分钟",
//                "§7受伤惩罚 §f-" + String.format("%.2f", explorationDifficulty.getHurtLumenCost()) + " §d幽匿光体 §8/ §71点伤害",
//                "§7受伤倍率 §fx" + String.format("%.2f", explorationDifficulty.getHurtDamageMultiple() * 100) + "§7%",
//                "§7带回物资 §f" + explorationDifficulty.getBringSlotAmount() + "§7格",
//                "§7奖励倍率 §f" + String.format("%.0f", explorationDifficulty.getRewardMultiple() * 100) + "§7%",
                " "
        );
    }
}
