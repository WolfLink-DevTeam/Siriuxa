package priv.mikkoayaka.minecraft.plugin.seriuxajourney;

import lombok.Getter;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.WolfirdPlugin;
import org.wolflink.minecraft.wolfird.framework.command.CmdHelp;
import org.wolflink.minecraft.wolfird.framework.command.WolfirdCommandAnalyser;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.VaultAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.MenuEventListener;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.command.Debug;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.command.OpenTaskMenu;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Config;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Lang;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.papi.TaskVariables;

public final class SeriuxaJourney extends WolfirdPlugin {

    @Getter
    private static SeriuxaJourney instance;

    @Override
    public void afterEnabled() {
        instance = this;
        // 加载配置文件和语言文件
        IOC.getBean(Config.class).load();
        IOC.getBean(Lang.class).load();

        IOC.getBean(VaultAPI.class); // 初始化 VaultAPI

        // 菜单事件处理
        IOC.getBean(MenuEventListener.class).setEnabled(true);

        // 注册指令
        IOC.getBean(WolfirdCommandAnalyser.class).register("sj");
        bindCommand(IOC.getBean(CmdHelp.class,"sj"));
        bindCommand(IOC.getBean(OpenTaskMenu.class));
        bindCommand(IOC.getBean(Debug.class));

        // 注册变量
        IOC.getBean(TaskVariables.class).register();
    }

    @Override
    public void beforeDisabled() {

    }
}
