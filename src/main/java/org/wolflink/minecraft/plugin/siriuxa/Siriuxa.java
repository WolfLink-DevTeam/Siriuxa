package org.wolflink.minecraft.plugin.siriuxa;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.plugin.siriuxa.api.VaultAPI;
import org.wolflink.minecraft.plugin.siriuxa.api.view.MenuEventListener;
import org.wolflink.minecraft.plugin.siriuxa.backpack.FiveSlotBackpack;
import org.wolflink.minecraft.plugin.siriuxa.command.*;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.ExplorationDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.TaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.difficulty.WheatTaskDifficulty;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.plugin.siriuxa.file.Lang;
import org.wolflink.minecraft.plugin.siriuxa.file.database.*;
import org.wolflink.minecraft.plugin.siriuxa.backpack.PlayerBackpack;
import org.wolflink.minecraft.plugin.siriuxa.task.listeners.CreatureDeathListener;
import org.wolflink.minecraft.plugin.siriuxa.task.listeners.CreatureSpawnListener;
import org.wolflink.minecraft.plugin.siriuxa.papi.ExplorationTaskVariables;
import org.wolflink.minecraft.plugin.siriuxa.sculkinfection.SculkInfection;
import org.wolflink.minecraft.plugin.siriuxa.task.listeners.*;
import org.wolflink.minecraft.plugin.siriuxa.task.listeners.huntcheck.HuntChecker;
import org.wolflink.minecraft.plugin.siriuxa.task.listeners.huntcheck.HuntValues;
import org.wolflink.minecraft.plugin.siriuxa.task.listeners.hurtcheck.HurtChecker;
import org.wolflink.minecraft.plugin.siriuxa.task.listeners.orecheck.OreChecker;
import org.wolflink.minecraft.plugin.siriuxa.task.listeners.orecheck.OreValues;
import org.wolflink.minecraft.wolfird.framework.WolfirdPlugin;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import org.wolflink.minecraft.wolfird.framework.command.CmdHelp;
import org.wolflink.minecraft.wolfird.framework.command.WolfirdCommandAnalyser;
import org.wolflink.minecraft.wolfird.framework.config.YamlConfig;

import java.util.ArrayList;
import java.util.List;

public final class Siriuxa extends WolfirdPlugin {

    /**
     * 初始化配置文件
     */
    private static final List<Class<? extends YamlConfig>> configs = new ArrayList<>();
    private static final List<Class<? extends FileDB>> databases = new ArrayList<>();
    private static final List<Class<? extends WolfirdListener>> globalListenerClasses = new ArrayList<>();
    private static final List<Class<? extends ConfigurationSerializable>> serializableClasses = new ArrayList<>();
    @Getter
    private static Siriuxa instance;

    static {
        configs.add(Config.class);
        configs.add(Lang.class);

        databases.add(InventoryDB.class);
        databases.add(OreDB.class);
        databases.add(HuntDB.class);
        databases.add(TaskRecordDB.class);

        globalListenerClasses.add(OreChecker.class);
        globalListenerClasses.add(HuntChecker.class);
        globalListenerClasses.add(HurtChecker.class);
        globalListenerClasses.add(FriendlyProtection.class);
        globalListenerClasses.add(FunctionBan.class);
        globalListenerClasses.add(TaskJoinQuitListener.class);
        globalListenerClasses.add(DeathDuringTask.class);
        globalListenerClasses.add(PlayerRespawn.class);
        globalListenerClasses.add(TPChecker.class);
        globalListenerClasses.add(SpawnChecker.class);
        globalListenerClasses.add(SculkSpreader.class);
        globalListenerClasses.add(CreatureSpawnListener.class);
        globalListenerClasses.add(CreatureDeathListener.class);
        globalListenerClasses.add(AttributeChecker.class);
        globalListenerClasses.add(StatListener.class);

        serializableClasses.add(PlayerBackpack.class);
        serializableClasses.add(PlayerTaskRecord.class);
        serializableClasses.add(OfflinePlayerRecord.class);
        serializableClasses.add(TaskDifficulty.class);
        serializableClasses.add(WheatTaskDifficulty.class);
        serializableClasses.add(ExplorationDifficulty.class);
        serializableClasses.add(FiveSlotBackpack.class);
    }

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
        bindCommand(IOC.getBean(OpenFiveSlotBackpack.class));

        notifier.setDebugMode(IOC.getBean(Config.class).get(ConfigProjection.DEBUG));

        // 注册变量
        IOC.getBean(ExplorationTaskVariables.class).register();

        // 注册全局监听器
        for (Class<? extends WolfirdListener> listenerClass : globalListenerClasses) {
            IOC.getBean(listenerClass).setEnabled(true);
        }
        IOC.getBean(SculkInfection.class).enable();
    }

    @Override
    public void beforeDisabled() {
        IOC.getBean(SculkInfection.class).disable();
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
}
