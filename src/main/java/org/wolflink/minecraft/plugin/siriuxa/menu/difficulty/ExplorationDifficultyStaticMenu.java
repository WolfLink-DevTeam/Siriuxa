package org.wolflink.minecraft.plugin.siriuxa.menu.difficulty;

import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;

import java.util.UUID;

public class ExplorationDifficultyStaticMenu extends DifficultyStaticMenu<ExplorationDifficulty> {
    public ExplorationDifficultyStaticMenu(UUID ownerUuid) {
        super(ownerUuid, ExplorationDifficulty.class);
    }
}
