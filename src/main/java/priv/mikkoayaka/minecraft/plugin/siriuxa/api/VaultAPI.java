package priv.mikkoayaka.minecraft.plugin.siriuxa.api;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.siriuxa.Siriuxa;
import priv.mikkoayaka.minecraft.plugin.siriuxa.utils.Notifier;

@Singleton
public class VaultAPI {
    @Getter
    private Economy economy;

    public VaultAPI() {
        if (!setupEconomy()) {
            Siriuxa.getInstance().getNotifier().error("未能找到 Vault 依赖！插件将停止工作。");
            Siriuxa.getInstance().getServer().getPluginManager().disablePlugin(Siriuxa.getInstance());
        }
    }

    private boolean setupEconomy() {
        if (Siriuxa.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Siriuxa.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public double getEconomy(OfflinePlayer offlinePlayer) {
        return economy.getBalance(offlinePlayer);
    }

    public boolean takeEconomy(OfflinePlayer offlinePlayer, double value) {
        EconomyResponse r = economy.withdrawPlayer(offlinePlayer, value);
        if (!r.transactionSuccess()) {
            Notifier.warn("尝试扣除玩家" + offlinePlayer.getName() + "账户余额时出现问题，数额：" + value);
            return false;
        }
        return true;
    }

    public boolean addEconomy(OfflinePlayer offlinePlayer, double value) {
        EconomyResponse r = economy.depositPlayer(offlinePlayer, value);
        if (!r.transactionSuccess()) {
            Notifier.warn("尝试增加玩家" + offlinePlayer.getName() + "账户余额时出现问题，数额：" + value);
            return false;
        }
        return true;
    }
}
