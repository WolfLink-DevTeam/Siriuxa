package org.wolflink.minecraft.plugin.siriuxa.file.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.utils.Notifier;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class FileDB {

    protected final File folder;
    private final Map<String, FileConfiguration> fileConfigurations = new HashMap<>();

    @Nullable
    public FileConfiguration getFileConfiguration(File file) {
        if(!fileConfigurations.containsKey(file.getAbsolutePath())) {
            Notifier.debug("没能找到文件"+file.getAbsolutePath()+"的FileConfiguration对象");
            return null;
        }
        return fileConfigurations.get(file.getAbsolutePath());
    }

    public FileDB(String folderName) {
        folder = new File(Siriuxa.getInstance().getDataFolder(), folderName);
        if (!folder.exists()) folder.mkdirs();
    }

    private void load(File folder) {
        File[] subFiles = folder.listFiles();
        if (subFiles == null) return;
        for (File subFile : subFiles) {
            if (subFile.isFile()) {
                try {
                    FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(subFile);
                    fileConfigurations.put(subFile.getAbsolutePath(), fileConfiguration);
                    Notifier.debug("加载了一个文件：" + subFile.getName());
                } catch (Exception ignore) {
                    Notifier.warn("加载文件：" + subFile.getName() + " 失败。");
                }
            } else if (subFile.isDirectory()) load(subFile);
        }
    }

    public FileConfiguration createAndLoad(File file) {
        File parent = file.getParentFile();
        if (!parent.exists()) parent.mkdirs();
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.error("在创建文件时出现异常。");
        }
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        fileConfigurations.put(file.getAbsolutePath(), fileConfiguration);
        return fileConfiguration;
    }

    public void load() {
        fileConfigurations.clear();
        load(folder);
    }

    public void save() {
        fileConfigurations.forEach((path, fileConfiguration) -> {
            try {
                fileConfiguration.save(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
                Notifier.error("在保存文件：" + path + " 时出现问题。");
            }
        });
    }
}
