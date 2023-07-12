package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.difficulty;

import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.Menu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.DifficultyRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;

public class DifficultyMenu extends Menu {

    private final DifficultyRepository difficultyRepository;
    public DifficultyMenu() {
        super(-1, "§0难度选择", 27);
        difficultyRepository = IOC.getBean(DifficultyRepository.class);
    }

    @Override
    public void overrideIcons() {
        int startIndex = 10;
        for (TaskDifficulty taskDifficulty : difficultyRepository.findAll()) {
            setIcon(startIndex++,new DifficultyIcon(taskDifficulty));
        }
    }
}
