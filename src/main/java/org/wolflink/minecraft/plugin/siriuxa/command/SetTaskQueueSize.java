package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.wolflink.common.ioc.IOC;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class SetTaskQueueSize extends WolfirdCommand {
    public SetTaskQueueSize() {
        super(true, true, true, "sx task queue set {size}", "设置任务队列最大数量");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        try {
            int size = Integer.parseInt(strings[0]);
            IOC.getBean(Config.class).update(ConfigProjection.TASK_QUEUE_SIZE.getPath(), size);
            Notifier.msg(commandSender, "设置成功");
        } catch (Exception ignore) {
            Notifier.msg(commandSender, "设置失败，请检查指令！");
        }
    }
}
