package priv.mikkoayaka.minecraft.plugin.seriuxajourney;

import lombok.Getter;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.WolfirdPlugin;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import org.wolflink.minecraft.wolfird.framework.command.CmdHelp;
import org.wolflink.minecraft.wolfird.framework.command.WolfirdCommandAnalyser;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.VaultAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view.MenuEventListener;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.command.*;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Config;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.ConfigProjection;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Lang;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.OreCache;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.monster.listener.MonsterListener;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.papi.TaskVariables;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.listener.FriendlyProtection;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.listener.FunctionBan;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.listener.HurtChecker;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.listener.orecheck.OreChecker;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.listener.orecheck.OreValues;

import java.util.ArrayList;
import java.util.List;

public final class SeriuxaJourney extends WolfirdPlugin {

    @Getter
    private static SeriuxaJourney instance;

    @Override
    public void afterEnabled() {
        instance = this;
        // 加载配置文件和语言文件
        IOC.getBean(Config.class).load();
        IOC.getBean(Lang.class).load();
        IOC.getBean(OreCache.class).load();

        IOC.getBean(VaultAPI.class); // 初始化 VaultAPI

        // 菜单事件处理
        IOC.getBean(MenuEventListener.class).setEnabled(true);

        // 注册指令
        IOC.getBean(WolfirdCommandAnalyser.class).register("sj");
        bindCommand(IOC.getBean(CmdHelp.class,"sj"));
        bindCommand(IOC.getBean(OpenTaskMenu.class));
        bindCommand(IOC.getBean(Debug.class));
        bindCommand(IOC.getBean(TeamInvite.class));
        bindCommand(IOC.getBean(TeamAccept.class));
        bindCommand(IOC.getBean(TeamDeny.class));
        bindCommand(IOC.getBean(TaskReady.class));
        bindCommand(IOC.getBean(TeamCreate.class));
        bindCommand(IOC.getBean(TeamInfo.class));

        notifier.setDebugMode(IOC.getBean(Config.class).get(ConfigProjection.DEBUG));

        // 注册变量
        IOC.getBean(TaskVariables.class).register();

        // 注册全局监听器
        for(Class<? extends WolfirdListener> listenerClass : globalListenerClasses) {
            IOC.getBean(listenerClass).setEnabled(true);
        }
    }

    @Override
    public void beforeDisabled() {
        IOC.getBean(OreValues.class).save();
        IOC.getBean(OreCache.class).save();
        IOC.getBean(Config.class).save();
        IOC.getBean(Lang.class).save();

        // 注销全局监听器
        for(Class<? extends WolfirdListener> listenerClass : globalListenerClasses) {
            IOC.getBean(listenerClass).setEnabled(false);
        }

    }
    /**
     * 注册全局监听器
     */
    private static final List<Class<? extends WolfirdListener>> globalListenerClasses = new ArrayList<>(){{
        add(OreChecker.class);
        add(HurtChecker.class);
        add(MonsterListener.class);
        add(FriendlyProtection.class);
        add(FunctionBan.class);
    }};

}
