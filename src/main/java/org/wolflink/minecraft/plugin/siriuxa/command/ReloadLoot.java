package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

import java.io.File;
import java.io.IOException;

/**
 * 重载有关于自定义战利品的配置文件
 */
@Singleton
public class ReloadLoot extends WolfirdCommand {
    private final JavaPlugin plugin;
    private final File configFile;
    private FileConfiguration config;

    public ReloadLoot(JavaPlugin plugin) {
        super(true, false, false, "sx reloadloot", "重载有关于自定义战利品的配置文件");
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), "loot.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof Player)) return; // 只有Player才可以运行这个命令
        if (!configFile.exists()) {
            plugin.saveResource("loot.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        try {
            config.save(configFile); // 保存配置文件
        } catch (IOException e) {
            return;
        }
        commandSender.sendMessage("配置文件重载成功");
    }
}
