package org.wolflink.minecraft.plugin.siriuxa.file.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;

import java.io.File;

@Singleton
public class LootDB extends FileDB {
    public LootDB() {
        super("loot");
    }

    public void reload() {
        File lootFile = new File(folder, "loot.yml");
        FileConfiguration fileConfiguration = getFileConfiguration(lootFile);
        if (fileConfiguration == null) fileConfiguration = createAndLoad(lootFile);
        try {
            fileConfiguration.save(lootFile);
            Notifier.debug("配置文件加载成功。");
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.debug("配置文件加载失败。");
        }
    }

    public void setLoot(EntityType entityType, ItemStack loot) {
        File lootFile = new File(folder, entityType.name() + ".yml");
        FileConfiguration fileConfiguration = getFileConfiguration(lootFile);
        if (fileConfiguration == null) fileConfiguration = createAndLoad(lootFile);
        fileConfiguration.set(entityType.name().toLowerCase(), loot);
        try {
            fileConfiguration.save(lootFile);
            Notifier.debug("实体类型为" + entityType.name() + "的战利品为" + loot + " 已保存。");
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.debug("尝试保存 实体类型为" + entityType.name() + "的战利品为" + loot + " 失败。");
        }
    }

    public void setDropChance(ItemStack loot, double dropChance) {
        File lootFile = new File(folder, loot + ".yml");
        FileConfiguration fileConfiguration = getFileConfiguration(lootFile);
        if (fileConfiguration == null) fileConfiguration = createAndLoad(lootFile);
        fileConfiguration.set(loot.toString(), dropChance);
        try {
            fileConfiguration.save(lootFile);
            Notifier.debug("战利品为" + loot + "的掉落概率为" + dropChance + " 已保存。");
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.debug("尝试保存 战利品为" + loot + "的掉落概率为" + dropChance + " 失败。");
        }
    }
}
