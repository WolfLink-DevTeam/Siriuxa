package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.difficulty;

import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.ExplorationDifficulty;

import java.util.UUID;

public class ExplorationDifficultyMenu extends DifficultyMenu<ExplorationDifficulty> {
    public ExplorationDifficultyMenu(UUID ownerUuid) {
        super(ownerUuid,ExplorationDifficulty.class);
    }
}
