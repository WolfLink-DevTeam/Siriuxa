package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.task.tasks.common.TaskRepository;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

@Singleton
public class Debug extends WolfirdCommand {
    @Inject
    private TaskRepository taskRepository;

    public Debug() {
        super(true, false, true, "sx debug", "开发者调试指令");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
    }
}
