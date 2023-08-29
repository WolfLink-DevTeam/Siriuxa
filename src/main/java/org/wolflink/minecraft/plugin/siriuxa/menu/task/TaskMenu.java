package org.wolflink.minecraft.plugin.siriuxa.menu.task;

import org.jetbrains.annotations.Nullable;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.view.BorderIcon;
import org.wolflink.minecraft.plugin.siriuxa.api.view.StaticMenu;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.menu.MenuService;
import org.wolflink.minecraft.plugin.siriuxa.menu.difficulty.ExplorationDifficultyMenu;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.icon.ChallengeTag;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.icon.CreateTask;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.icon.SafeSlotIcon;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.icon.SelectDifficulty;

import java.util.UUID;

public class TaskMenu extends StaticMenu {

    private final MenuService menuService;

    public TaskMenu(UUID uuid) {
        super(uuid, "§0§l任务菜单", 27);
        menuService = IOC.getBean(MenuService.class);
    }

    @Override
    public void overrideIcons() {
        BorderIcon borderIcon = IOC.getBean(BorderIcon.class);
        setIcon(9, borderIcon);
        setIcon(15, borderIcon);
        setIcon(17, borderIcon);
        setIcon(10, new SelectDifficulty(this));
        setIcon(11, new SafeSlotIcon(this));
        setIcon(12, new ChallengeTag());
        setIcon(16, new CreateTask(this));
    }

    @Nullable
    public ExplorationDifficulty getSelectedDifficulty() {
        if (getOwner() == null || !getOwner().isOnline()) return null;
        ExplorationDifficultyMenu explorationDifficultyMenu = menuService.findMenu(getOwner(), ExplorationDifficultyMenu.class);
        return explorationDifficultyMenu.getSelectedDifficulty();
    }
}
