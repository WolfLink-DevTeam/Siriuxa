package org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.smartai;

import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.composable.components.ornaments.TaskOrnament;

@Singleton
public class OrnamentSmartAI extends TaskOrnament {
    public OrnamentSmartAI() {
        super("意识觉醒", "§c", "受到严重感染的怪物们，似乎变得格外聪明，它们学会了合作与思考，思考怎样一步步吃掉冒险者的大脑...");
    }

    @Override
    public void enable() {
        IOC.getBean(SmartAIListener.class).setEnabled(true);
    }

    @Override
    public void disable() {
        IOC.getBean(SmartAIListener.class).setEnabled(false);
    }
}
