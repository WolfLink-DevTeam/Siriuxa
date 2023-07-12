package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;

@Singleton
public class VaultAPI {
    @Getter
    private Economy economy;

    public VaultAPI() {
        if(!setupEconomy()) {
            SeriuxaJourney.getInstance().getNotifier().error("未能找到 Vault 依赖！插件将停止工作。");
            SeriuxaJourney.getInstance().getServer().getPluginManager().disablePlugin(SeriuxaJourney.getInstance());
        }
    }
    private boolean setupEconomy() {
        if (SeriuxaJourney.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = SeriuxaJourney.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
}
