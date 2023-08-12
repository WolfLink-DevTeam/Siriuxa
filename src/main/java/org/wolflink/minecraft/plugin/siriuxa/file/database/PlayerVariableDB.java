package org.wolflink.minecraft.plugin.siriuxa.file.database;

import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;

import java.io.File;
import java.util.Objects;

@Singleton
public class PlayerVariableDB extends FileDB{
    protected PlayerVariableDB(){
        super("player_var");
    }
    @NonNull
    public PlayerVariables get(OfflinePlayer offlinePlayer) {
        File file = new File(folder,offlinePlayer.getName()+".yml");
        FileConfiguration fileConfiguration = getFileConfiguration(file);
        if(fileConfiguration == null) {
            return new PlayerVariables(offlinePlayer.getUniqueId());
        }
        // 存在就一定不会为null
        return (PlayerVariables) Objects.requireNonNull(fileConfiguration.get("data"));
    }
    public void save(OfflinePlayer offlinePlayer,PlayerVariables playerVariables) {
        File file = new File(folder,offlinePlayer.getName()+".yml");
        FileConfiguration fileConfiguration = getFileConfiguration(file);
        if(fileConfiguration == null) {
            fileConfiguration = createAndLoad(file);
        }
        fileConfiguration.set("data",playerVariables);
        save(file);
        Notifier.debug("玩家 "+offlinePlayer.getName()+" 的变量数据已保存");
    }
}
