package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

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
    public double getEconomy(OfflinePlayer offlinePlayer) {
        return economy.getBalance(offlinePlayer);
    }
    public boolean takeEconomy(OfflinePlayer offlinePlayer,double value) {
        EconomyResponse r = economy.withdrawPlayer(offlinePlayer,value);
        if(!r.transactionSuccess()) {
            Notifier.warn("尝试扣除玩家"+offlinePlayer.getName()+"账户余额时出现问题，数额："+value);
            return false;
        }
        return true;
    }
    public boolean addEconomy(OfflinePlayer offlinePlayer,double value) {
        EconomyResponse r = economy.depositPlayer(offlinePlayer,value);
        if(!r.transactionSuccess()) {
            Notifier.warn("尝试增加玩家"+offlinePlayer.getName()+"账户余额时出现问题，数额："+value);
            return false;
        }
        return true;
    }
}
