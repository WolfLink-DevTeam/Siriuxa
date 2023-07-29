package org.wolflink.minecraft.plugin.siriuxa.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.wolflink.minecraft.plugin.siriuxa.api.view.StaticMenu;
import org.wolflink.minecraft.plugin.siriuxa.menu.difficulty.ExplorationDifficultyStaticMenu;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.ExplorationBackpackStaticMenu;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.TaskRecordStaticMenu;
import org.wolflink.minecraft.plugin.siriuxa.menu.task.TaskStaticMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMenuContainer {

    private static final Map<UUID, PlayerMenuContainer> instanceMap = new HashMap<>();
    private final Map<Class<? extends StaticMenu>, StaticMenu> menuMap = new HashMap<>();

    public PlayerMenuContainer(UUID uuid) {
        menuMap.put(TaskStaticMenu.class, new TaskStaticMenu(uuid));
        menuMap.put(ExplorationDifficultyStaticMenu.class, new ExplorationDifficultyStaticMenu(uuid));
        menuMap.put(TaskRecordStaticMenu.class, new TaskRecordStaticMenu(uuid));
        menuMap.put(ExplorationBackpackStaticMenu.class, new ExplorationBackpackStaticMenu(uuid));
    }

    @Nullable
    public static StaticMenu findMenu(Player player, Class<? extends StaticMenu> menuClass) {
        PlayerMenuContainer container = instanceMap.get(player.getUniqueId());
        if (container == null) {
            instanceMap.put(player.getUniqueId(), new PlayerMenuContainer(player.getUniqueId()));
            container = instanceMap.get(player.getUniqueId());
        }
        return container.menuMap.get(menuClass);
    }

    @Nullable
    public static StaticMenu findMenu(Player player, String title) {
        PlayerMenuContainer container = instanceMap.get(player.getUniqueId());
        if (container == null) {
            instanceMap.put(player.getUniqueId(), new PlayerMenuContainer(player.getUniqueId()));
            container = instanceMap.get(player.getUniqueId());
        }
        for (StaticMenu staticMenu : container.menuMap.values()) {
            if (staticMenu.getTitle().equals(title)) return staticMenu;
        }
        return null;
    }
}
