package org.wolflink.minecraft.plugin.siriuxa;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.command.*;
import org.wolflink.minecraft.plugin.siriuxa.file.database.*;
import org.wolflink.minecraft.plugin.siriuxa.invbackup.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.sculkinfection.SculkInfection;
import org.wolflink.minecraft.plugin.siriuxa.task.common.listener.*;
import org.wolflink.minecraft.plugin.siriuxa.task.common.listener.huntcheck.HuntChecker;
import org.wolflink.minecraft.plugin.siriuxa.task.common.listener.huntcheck.HuntValues;
import org.wolflink.minecraft.wolfird.framework.WolfirdPlugin;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import org.wolflink.minecraft.wolfird.framework.command.CmdHelp;
import org.wolflink.minecraft.wolfird.framework.command.WolfirdCommandAnalyser;
import org.wolflink.minecraft.wolfird.framework.config.YamlConfig;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.view.MenuEventListener;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.plugin.siriuxa.file.Lang;
import org.wolflink.minecraft.plugin.siriuxa.papi.TaskVariables;
import org.wolflink.minecraft.plugin.siriuxa.task.common.listener.orecheck.OreChecker;
import org.wolflink.minecraft.plugin.siriuxa.task.common.listener.orecheck.OreValues;

import java.util.ArrayList;
import java.util.List;

public final class Siriuxa extends WolfirdPlugin {

    @Getter
    private static Siriuxa instance;

    @Override
    public void afterEnabled() {
        instance = this;
        serializableClasses.forEach(ConfigurationSerialization::registerClass);
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
        IOC.getBean(WolfirdCommandAnalyser.class).register("sx");
        bindCommand(IOC.getBean(CmdHelp.class, "sx"));
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
        bindCommand(IOC.getBean(TaskHistory.class));
        bindCommand(IOC.getBean(GoLobby.class));

        notifier.setDebugMode(IOC.getBean(Config.class).get(ConfigProjection.DEBUG));

        // 注册变量
        IOC.getBean(TaskVariables.class).register();

        // 注册全局监听器
        for (Class<? extends WolfirdListener> listenerClass : globalListenerClasses) {
            IOC.getBean(listenerClass).setEnabled(true);
        }
        IOC.getBean(SculkInfection.class).setEnabled(true);
    }

    @Override
    public void beforeDisabled() {
        IOC.getBean(SculkInfection.class).setEnabled(false);
        IOC.getBean(OreValues.class).doSave();
        IOC.getBean(HuntValues.class).doSave();

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

        serializableClasses.forEach(ConfigurationSerialization::unregisterClass);
    }

    /**
     * 初始化配置文件
     */
    private static final List<Class<? extends YamlConfig>> configs = new ArrayList<>() {{
        add(Config.class);
        add(Lang.class);
    }};
    private static final List<Class<? extends FileDB>> databases = new ArrayList<>() {{
        add(InventoryDB.class);
        add(OreDB.class);
        add(HuntDB.class);
        add(TaskRecordDB.class);
    }};
    /**
     * 注册全局监听器
     */
    private static final List<Class<? extends WolfirdListener>> globalListenerClasses = new ArrayList<>() {{
        add(OreChecker.class);
        add(HuntChecker.class);
        add(HurtChecker.class);
        add(FriendlyProtection.class);
        add(FunctionBan.class);
        add(JoinQuitListener.class);
        add(DeathDuringTask.class);
        add(PlayerRespawn.class);
        add(TPChecker.class);
        add(SpawnChecker.class);
        add(SculkSpreader.class);
    }};
    private static final List<Class<? extends ConfigurationSerializable>> serializableClasses = new ArrayList<>(){{
       add(PlayerBackpack.class);
       add(PlayerTaskRecord.class);
       add(OfflinePlayerRecord.class);
    }};

}
