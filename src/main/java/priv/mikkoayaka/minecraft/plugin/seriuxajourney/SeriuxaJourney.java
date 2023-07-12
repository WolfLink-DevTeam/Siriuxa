package priv.mikkoayaka.minecraft.plugin.seriuxajourney;

import lombok.Getter;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.WolfirdPlugin;
import org.wolflink.minecraft.wolfird.framework.command.WolfirdCommandAnalyser;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.VaultAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.command.Debug;

public final class SeriuxaJourney extends WolfirdPlugin {

    @Getter
    private static SeriuxaJourney instance;

    @Override
    public void afterEnabled() {
        instance = this;
        IOC.getBean(VaultAPI.class); // 初始化 VaultAPI

        // 注册指令
        IOC.getBean(WolfirdCommandAnalyser.class).register("sj");
        bindCommand(IOC.getBean(Debug.class));
    }

    @Override
    public void beforeDisabled() {

    }
}
