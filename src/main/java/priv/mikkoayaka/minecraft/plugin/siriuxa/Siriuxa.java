package priv.mikkoayaka.minecraft.plugin.siriuxa;

import lombok.Getter;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.WolfirdPlugin;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import org.wolflink.minecraft.wolfird.framework.command.CmdHelp;
import org.wolflink.minecraft.wolfird.framework.command.WolfirdCommandAnalyser;
import org.wolflink.minecraft.wolfird.framework.config.YamlConfig;
import priv.mikkoayaka.minecraft.plugin.siriuxa.api.VaultAPI;
import priv.mikkoayaka.minecraft.plugin.siriuxa.api.view.MenuEventListener;
import priv.mikkoayaka.minecraft.plugin.siriuxa.command.*;
import priv.mikkoayaka.minecraft.plugin.siriuxa.file.Config;
import priv.mikkoayaka.minecraft.plugin.siriuxa.file.ConfigProjection;
import priv.mikkoayaka.minecraft.plugin.siriuxa.file.Lang;
import priv.mikkoayaka.minecraft.plugin.siriuxa.file.OreCache;
import priv.mikkoayaka.minecraft.plugin.siriuxa.file.database.FileDB;
import priv.mikkoayaka.minecraft.plugin.siriuxa.file.database.InventoryDB;
import priv.mikkoayaka.minecraft.plugin.siriuxa.file.database.OreDB;
import priv.mikkoayaka.minecraft.plugin.siriuxa.papi.TaskVariables;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.listener.FriendlyProtection;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.listener.FunctionBan;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.listener.HurtChecker;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.listener.orecheck.OreChecker;
import priv.mikkoayaka.minecraft.plugin.siriuxa.task.common.listener.orecheck.OreValues;

import java.util.ArrayList;
import java.util.List;

public final class Siriuxa extends WolfirdPlugin {

    @Getter
    private static Siriuxa instance;

    @Override
    public void afterEnabled() {
        instance = this;
        // 加载配置文件和语言文件
        for (Class<? extends YamlConfig> config : configs) {
            IOC.getBean(config).load();
        }
        // 加载数据库
        for (Class<? extends FileDB> db : databases) {
            IOC.getBean(db).load();
        }

        IOC.getBean(VaultAPI.class); // 初始化 VaultAPI

        // 菜单事件处理
        IOC.getBean(MenuEventListener.class).setEnabled(true);

        // 注册指令
        IOC.getBean(WolfirdCommandAnalyser.class).register("sj");
        bindCommand(IOC.getBean(CmdHelp.class, "sj"));
        bindCommand(IOC.getBean(OpenTaskMenu.class));
        bindCommand(IOC.getBean(Debug.class));
        bindCommand(IOC.getBean(TeamInvite.class));
        bindCommand(IOC.getBean(TeamAccept.class));
        bindCommand(IOC.getBean(TeamDeny.class));
        bindCommand(IOC.getBean(TaskReady.class));
        bindCommand(IOC.getBean(TeamCreate.class));
        bindCommand(IOC.getBean(TeamInfo.class));
        bindCommand(IOC.getBean(TeamKick.class));
        bindCommand(IOC.getBean(TeamLeave.class));

        notifier.setDebugMode(IOC.getBean(Config.class).get(ConfigProjection.DEBUG));

        // 注册变量
        IOC.getBean(TaskVariables.class).register();

        // 注册全局监听器
        for (Class<? extends WolfirdListener> listenerClass : globalListenerClasses) {
            IOC.getBean(listenerClass).setEnabled(true);
        }
    }

    @Override
    public void beforeDisabled() {
        IOC.getBean(OreValues.class).save();

        // 保存数据库
        for (Class<? extends FileDB> db : databases) {
            IOC.getBean(db).save();
        }

        for (Class<? extends YamlConfig> config : configs) {
            IOC.getBean(config).save();
        }

        // 注销全局监听器
        for (Class<? extends WolfirdListener> listenerClass : globalListenerClasses) {
            IOC.getBean(listenerClass).setEnabled(false);
        }

    }

    /**
     * 初始化配置文件
     */
    private static final List<Class<? extends YamlConfig>> configs = new ArrayList<>() {{
        add(OreCache.class);
        add(Config.class);
        add(Lang.class);
    }};
    private static final List<Class<? extends FileDB>> databases = new ArrayList<>() {{
        add(InventoryDB.class);
        add(OreDB.class);
    }};
    /**
     * 注册全局监听器
     */
    private static final List<Class<? extends WolfirdListener>> globalListenerClasses = new ArrayList<>() {{
        add(OreChecker.class);
        add(HurtChecker.class);
        add(FriendlyProtection.class);
        add(FunctionBan.class);
    }};

}