package priv.mikkoayaka.minecraft.plugin.siriuxa.api;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.Singleton;

@Singleton
public class SerializeAPI {
    public String itemStack(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", itemStack);
        return config.saveToString();
    }

    public ItemStack itemStack(String str) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(str);
            return config.getItemStack("item");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
