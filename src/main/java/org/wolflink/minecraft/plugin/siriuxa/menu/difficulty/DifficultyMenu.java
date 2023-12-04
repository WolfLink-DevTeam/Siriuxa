package org.wolflink.minecraft.plugin.siriuxa.menu.difficulty;

import lombok.Getter;
import lombok.Setter;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.view.StaticMenu;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.DifficultyRepository;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;

import java.util.UUID;

@Getter
@Setter
public abstract class DifficultyMenu extends StaticMenu {

    private TaskDifficulty selectedDifficulty = null;

    protected DifficultyMenu(UUID ownerUuid) {
        super(ownerUuid, "§0§l难度选择", 27);
    }

    @Override
    public void overrideIcons() {
        DifficultyRepository difficultyRepository = IOC.getBean(DifficultyRepository.class);
        int startIndex = 10;
        for (TaskDifficulty difficulty : difficultyRepository.findAll()) {
            setIcon(startIndex++, new DifficultyIcon(this, difficulty));
        }
    }
}
