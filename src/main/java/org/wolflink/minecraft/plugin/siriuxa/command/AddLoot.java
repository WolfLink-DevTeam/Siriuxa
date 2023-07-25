package org.wolflink.minecraft.plugin.siriuxa.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 在服务器中动态修改怪物掉落战利品的概率
 */
@Singleton
public class AddLoot extends WolfirdCommand {
    private final JavaPlugin plugin;
    private final File configFile;
    private final YamlConfiguration config;

    /**
     * 将配置文件加载到YamlConfiguration对象中
     */
    public AddLoot(JavaPlugin plugin) {
        super(true, false, false, "sx addloot {entityType}", "动态修改怪物掉落战利品的概率");
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), "loot.yml");
        config = YamlConfiguration.loadConfiguration(configFile); // 加载配置文件
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        if (!configFile.exists()) {
            plugin.saveResource("loot.yml", false);
        }

        if (!(commandSender instanceof Player)) return; // 只有Player才可以运行这个命令
        if (strings.length != 1) return; // 输入的指令格式有误
        EntityType entityType = EntityType.valueOf(strings[0].toUpperCase()); // 实体类型
        //if (entityType == null) return; // 无效的实体类型

        ItemStack itemStack = ((Player) commandSender).getInventory().getItemInMainHand(); // 手持物品
        if (itemStack.getType() == Material.AIR) return; // 必须手持物品
        List<ItemStack> loot = (List<ItemStack>) config.getList(entityType.name(), new ArrayList<>()); // 将命令执行者手持的物品堆叠(ItemStack)加入到怪物的死亡掉落战利品中
        loot.add(itemStack);

        config.set(entityType.name(), loot); // 向配置文件中添加实体类型及其对应的战利品
        double dropChance = Math.random();// 怪物死亡时有dropChance(0-1的随机数)的概率掉落
        config.set(loot.toString(), dropChance); // 向配置文件中添加战利品及其对应的掉落概率

        try {
            config.save(configFile); // 保存配置文件
        } catch (IOException e) {
            return;
        }

        commandSender.sendMessage("添加" + itemStack.getType().name() + "为" + entityType.name() + "的战利品，掉落概率为" + dropChance + "%."); // 添加成功的信息
    }
}
