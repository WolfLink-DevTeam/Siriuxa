package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.Framework;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class WorldEditAPI {

    File schemFolder;
    public WorldEditAPI() {
        schemFolder = new File(Framework.getInstance().getDataFolder(), "seriuxajourney_schematic");
        if(!schemFolder.exists()) {
            schemFolder.mkdirs();
        }
    }
    public List<File> getWorkingUnitSchemFiles() {
        List<File> schemFiles = new ArrayList<>();
        File[] subFiles = schemFolder.listFiles();
        if(subFiles == null) return schemFiles;
        for (File schemFile : subFiles) {
            if(schemFile.getName().startsWith("working_unit")) {
                schemFiles.add(schemFile);
            }
        }
        return schemFiles;
    }

    /**
     * 从可用的工作单元中随机挑选一个生成
     */
    public void pasteWorkingUnit(Location center) {
        List<File> workingUnitFiles = getWorkingUnitSchemFiles();
        if(workingUnitFiles.size() == 0) return;
        File schem = workingUnitFiles.get((int) (workingUnitFiles.size() * Math.random()));
        pasteSchem(schem,center);
    }
    public void pasteSchem(File schem, Location center) {
        try(EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(center.getWorld()))) {
            Clipboard clipboard = ClipboardFormats.findByFile(schem)
                    .getReader(new FileInputStream(schem))
                    .read();
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(center.getBlockX(),center.getBlockY(), center.getBlockZ()))
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.error("在拷贝结构时出现异常，相关结构文件："+schem.getName());
        }
    }
}
