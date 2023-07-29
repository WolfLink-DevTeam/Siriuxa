package org.wolflink.minecraft.plugin.siriuxa.menu;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.wolflink.minecraft.plugin.siriuxa.api.view.Menu;
import org.wolflink.minecraft.plugin.siriuxa.menu.difficulty.ExplorationDifficultyMenu;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.ExplorationBackpackMenu;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.TaskRecordMenu;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.TaskMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMenuContainer {

    private static final Map<UUID, PlayerMenuContainer> instanceMap = new HashMap<>();
    private final Map<Class<? extends Menu>, Menu> menuMap = new HashMap<>();

    public PlayerMenuContainer(UUID uuid) {
        menuMap.put(TaskMenu.class, new TaskMenu(uuid));
        menuMap.put(ExplorationDifficultyMenu.class, new ExplorationDifficultyMenu(uuid));
        menuMap.put(TaskRecordMenu.class, new TaskRecordMenu(uuid));
        menuMap.put(ExplorationBackpackMenu.class, new ExplorationBackpackMenu(uuid));
    }

    @Nullable
    public static Menu findMenu(@NonNull Player player, Class<? extends Menu> menuClass) {
        PlayerMenuContainer container = instanceMap.get(player.getUniqueId());
        if (container == null) {
            instanceMap.put(player.getUniqueId(), new PlayerMenuContainer(player.getUniqueId()));
            container = instanceMap.get(player.getUniqueId());
        }
        return container.menuMap.get(menuClass);
    }

    @Nullable
    public static Menu findMenu(@NonNull Player player, String title) {
        PlayerMenuContainer container = instanceMap.get(player.getUniqueId());
        if (container == null) {
            instanceMap.put(player.getUniqueId(), new PlayerMenuContainer(player.getUniqueId()));
            container = instanceMap.get(player.getUniqueId());
        }
        for (Menu staticMenu : container.menuMap.values()) {
            if (staticMenu.getTitle().equals(title)) return staticMenu;
        }
        return null;
    }
}
