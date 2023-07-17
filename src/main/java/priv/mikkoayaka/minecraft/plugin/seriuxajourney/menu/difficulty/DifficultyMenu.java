package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.difficulty;

import lombok.Getter;
import lombok.Setter;
import org.wolflink.common.ioc.IOC;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.Menu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.DifficultyRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.TaskDifficulty;

import java.util.UUID;

public abstract class DifficultyMenu<T extends TaskDifficulty> extends Menu {

    private final Class<T> difficultyClass;
    @Setter
    @Getter
    private T selectedDifficulty = null;

    public DifficultyMenu(UUID ownerUuid, Class<T> difficultyClass) {
        super(ownerUuid, -1, "§0§l难度选择", 27);
        this.difficultyClass = difficultyClass;
    }

    @Override
    public void overrideIcons() {
        DifficultyRepository difficultyRepository = IOC.getBean(DifficultyRepository.class);
        int startIndex = 10;
        for (T difficulty : difficultyRepository.findByType(difficultyClass)) {
            setIcon(startIndex++, new DifficultyIcon<>(this, difficulty));
        }
    }
}
