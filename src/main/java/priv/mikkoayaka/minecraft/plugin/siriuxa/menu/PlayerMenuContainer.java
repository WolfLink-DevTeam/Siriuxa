package priv.mikkoayaka.minecraft.plugin.siriuxa.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import priv.mikkoayaka.minecraft.plugin.siriuxa.api.view.Menu;
import priv.mikkoayaka.minecraft.plugin.siriuxa.menu.difficulty.ExplorationDifficultyMenu;
import priv.mikkoayaka.minecraft.plugin.siriuxa.menu.task.TaskMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMenuContainer {

    private static final Map<UUID, PlayerMenuContainer> instanceMap = new HashMap<>();
    private final Map<Class<? extends Menu>, Menu> menuMap = new HashMap<>();

    public PlayerMenuContainer(UUID uuid) {
        menuMap.put(TaskMenu.class, new TaskMenu(uuid));
        menuMap.put(ExplorationDifficultyMenu.class, new ExplorationDifficultyMenu(uuid));
    }

    @Nullable
    public static Menu findMenu(Player player, Class<? extends Menu> menuClass) {
        PlayerMenuContainer container = instanceMap.get(player.getUniqueId());
        if (container == null) {
            instanceMap.put(player.getUniqueId(), new PlayerMenuContainer(player.getUniqueId()));
            container = instanceMap.get(player.getUniqueId());
        }
        return container.menuMap.get(menuClass);
    }

    @Nullable
    public static Menu findMenu(Player player, String title) {
        PlayerMenuContainer container = instanceMap.get(player.getUniqueId());
        if (container == null) {
            instanceMap.put(player.getUniqueId(), new PlayerMenuContainer(player.getUniqueId()));
            container = instanceMap.get(player.getUniqueId());
        }
        for (Menu menu : container.menuMap.values()) {
            if (menu.getTitle().equals(title)) return menu;
        }
        return null;
    }
}
