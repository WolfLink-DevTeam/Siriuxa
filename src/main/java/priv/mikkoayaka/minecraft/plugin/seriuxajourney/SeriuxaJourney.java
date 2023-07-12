package priv.mikkoayaka.minecraft.plugin.seriuxajourney;

import lombok.Getter;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.WolfirdPlugin;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.VaultAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.command.Debug;

public final class SeriuxaJourney extends WolfirdPlugin {

    @Getter
    private static SeriuxaJourney instance;

    @Override
    public void afterEnabled() {
        instance = this;
        IOC.getBean(VaultAPI.class); // 初始化 VaultAPI
        bindCommand(IOC.getBean(Debug.class)); // 注册指令
    }

    @Override
    public void beforeDisabled() {

    }
}
