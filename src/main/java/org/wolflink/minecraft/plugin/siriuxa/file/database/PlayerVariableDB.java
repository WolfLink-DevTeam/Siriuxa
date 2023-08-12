package org.wolflink.minecraft.plugin.siriuxa.file.database;

import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.api.Notifier;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PlayerVariableDB extends FileDB{
    public PlayerVariableDB(){
        super("player_var");
    }
    private final Map<UUID,PlayerVariables> cache = new ConcurrentHashMap<>();
    @NonNull
    public PlayerVariables get(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        if(cache.containsKey(uuid)) return cache.get(uuid);
        else {
            PlayerVariables playerVariables = new PlayerVariables(uuid);
            save(offlinePlayer,playerVariables);
            return playerVariables;
        }
//        File file = new File(folder,offlinePlayer.getName()+".yml");
//        FileConfiguration fileConfiguration = getFileConfiguration(file);
//        if(fileConfiguration == null) {
//            PlayerVariables playerVariables = new PlayerVariables(offlinePlayer.getUniqueId());
//            save(offlinePlayer,playerVariables);
//            return playerVariables;
//        }
//        // 存在就一定不会为null
//        return (PlayerVariables) Objects.requireNonNull(fileConfiguration.get("data"));
    }
    public void save(OfflinePlayer offlinePlayer,PlayerVariables playerVariables) {
        File file = new File(folder,offlinePlayer.getName()+".yml");
        FileConfiguration fileConfiguration = getFileConfiguration(file);
        if(fileConfiguration == null) {
            fileConfiguration = createAndLoad(file);
        }
        fileConfiguration.set("data",playerVariables);
        save(file);
        cache.put(offlinePlayer.getUniqueId(),playerVariables);
        Notifier.debug("玩家 "+offlinePlayer.getName()+" 的变量数据已保存");
    }
}
