package priv.mikkoayaka.minecraft.plugin.seriuxajourney;

import lombok.Getter;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.WolfirdPlugin;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.VaultAPI;

public final class SeriuxaJourney extends WolfirdPlugin {

    @Getter
    private static SeriuxaJourney instance;

    @Override
    public void afterEnabled() {
        instance = this;
        IOC.getBean(VaultAPI.class); // 初始化 VaultAPI
    }

    @Override
    public void beforeDisabled() {

    }
}
