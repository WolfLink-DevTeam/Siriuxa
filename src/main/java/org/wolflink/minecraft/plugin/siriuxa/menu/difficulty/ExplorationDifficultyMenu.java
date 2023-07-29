package org.wolflink.minecraft.plugin.siriuxa.menu.difficulty;

import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;

import java.util.UUID;

public class ExplorationDifficultyMenu extends DifficultyMenu<ExplorationDifficulty> {
    public ExplorationDifficultyMenu(UUID ownerUuid) {
        super(ownerUuid, ExplorationDifficulty.class);
    }
}
