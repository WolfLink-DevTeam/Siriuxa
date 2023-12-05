package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.safeworking;

import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.TaskOrnament;

@Singleton
public class OrnamentSafeWorking extends TaskOrnament {
    public OrnamentSafeWorking() {
        super("安全作业", "§a", "由于空降区域的幽匿感染已被清理，空港特别批准了冒险者们携带自己的装备前往地面进行勘探。");
    }

    @Override
    public void enable() {
        IOC.getBean(SafeWorkingListener.class).setEnabled(true);
    }

    @Override
    public void disable() {
        IOC.getBean(SafeWorkingListener.class).setEnabled(false);
    }
}
