package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.World;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.region.TaskRegion;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class LocalSessionHolder {
    private final Map<LocationCommandSender,LocalSession> localSessionMap = new HashMap<>();

    @Nullable
    public LocalSession getLocalSession(LocationCommandSender locationCommandSender) {

        World world = locationCommandSender.getLocation().getWorld();
        if(world == null)return null;
        if(localSessionMap.containsKey(locationCommandSender)) return localSessionMap.get(locationCommandSender);
        LocalSession localSession = new LocalSession(WorldEdit.getInstance().getConfiguration());
        localSession.setWorldOverride(BukkitAdapter.adapt(world));
        localSessionMap.put(locationCommandSender,localSession);
        return localSession;
    }
}
