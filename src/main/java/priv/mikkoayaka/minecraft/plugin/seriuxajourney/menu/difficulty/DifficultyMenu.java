package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.difficulty;

import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.Menu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.DifficultyKey;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.DifficultyRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.ExplorationDifficulty;

public class DifficultyMenu extends Menu {

    public DifficultyMenu() {
        super(-1, "§0§l难度选择", 27);
    }

    @Override
    public void overrideIcons() {
        DifficultyRepository difficultyRepository = IOC.getBean(DifficultyRepository.class);
        int startIndex = 10;
        for (ExplorationDifficulty explorationDifficulty : difficultyRepository.findByType(ExplorationDifficulty.class)) {
            setIcon(startIndex++,new DifficultyIcon(explorationDifficulty));
        }
    }
}
