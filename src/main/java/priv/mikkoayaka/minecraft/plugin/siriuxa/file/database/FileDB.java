package priv.mikkoayaka.minecraft.plugin.siriuxa.file.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import priv.mikkoayaka.minecraft.plugin.siriuxa.Siriuxa;
import priv.mikkoayaka.minecraft.plugin.siriuxa.utils.Notifier;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class FileDB {

    protected final File folder;
    private final Map<File, FileConfiguration> fileConfigurations = new HashMap<>();

    @Nullable
    public FileConfiguration getFileConfiguration(File file) {
        return fileConfigurations.get(file);
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
                fileConfigurations.put(subFile, YamlConfiguration.loadConfiguration(subFile));
                Notifier.debug("加载了一个文件：" + subFile.getName());
            } else load(subFile);
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
        fileConfigurations.put(file, fileConfiguration);
        return fileConfiguration;
    }

    public void load() {
        fileConfigurations.clear();
        load(folder);
    }

    public void save() {
        fileConfigurations.forEach((file, fileConfiguration) -> {
            try {
                fileConfiguration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
                Notifier.error("在保存文件：" + file.getName() + " 时出现问题。");
            }
        });
    }
}
