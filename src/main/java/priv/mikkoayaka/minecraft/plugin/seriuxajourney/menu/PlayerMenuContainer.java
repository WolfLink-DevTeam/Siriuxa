package priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.InventoryMenu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.menu.task.TaskMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMenuContainer {

    private static final Map<UUID, PlayerMenuContainer> instanceMap = new HashMap<>();
    private final Map<Class<? extends InventoryMenu>,InventoryMenu> menuMap = new HashMap<>();

    public PlayerMenuContainer(UUID uuid) {
        menuMap.put(TaskMenu.class,new TaskMenu(uuid));
    }
    @Nullable
    public static InventoryMenu findMenu(Player player,Class<? extends InventoryMenu> menuClass) {
        PlayerMenuContainer container = instanceMap.get(player.getUniqueId());
        if(container == null) {
            instanceMap.put(player.getUniqueId(),new PlayerMenuContainer(player.getUniqueId()));
            container = instanceMap.get(player.getUniqueId());
        }
        return container.menuMap.get(menuClass);
    }
}
