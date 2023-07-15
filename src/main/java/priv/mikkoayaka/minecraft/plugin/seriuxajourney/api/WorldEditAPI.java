package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitCommandSender;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.SessionOwner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.Framework;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.EvacuationZone;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

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
        return getSchemFilesByPrefix("working_unit");
    }
    public List<File> getSchemFilesByPrefix(String prefix) {
        List<File> schemFiles = new ArrayList<>();
        File[] subFiles = schemFolder.listFiles();
        if(subFiles == null) return schemFiles;
        for (File schemFile : subFiles) {
            if(schemFile.getName().startsWith(prefix)) {
                schemFiles.add(schemFile);
            }
        }
        return schemFiles;
    }
    public List<File> getEvacuationUnitSchemFiles() {
        return getSchemFilesByPrefix("evacuation_unit");
    }

    /**
     * 从可用的工作单元中随机挑选一个生成
     */
    public void pasteWorkingUnit(Location center) {
        List<File> workingUnitFiles = getWorkingUnitSchemFiles();
        if(workingUnitFiles.size() == 0) {
            Notifier.error("没有找到可用的工作单元结构");
            return;
        }
        File schem = workingUnitFiles.get((int) (workingUnitFiles.size() * Math.random()));
        pasteSchem(schem,center);
    }
    public void pasteEvacuationUnit(EditSession editSession,Location center) {
        List<File> evacuationUnitFiles = getEvacuationUnitSchemFiles();
        if(evacuationUnitFiles.size() == 0) {
            Notifier.error("没有找到可用的撤离单元结构");
            return;
        }
        File schem = evacuationUnitFiles.get((int) (evacuationUnitFiles.size() * Math.random()));
        pasteSchem(editSession,schem,center);
    }
    public void undoEvacuationUnit(EditSession editSession) {
        editSession.undo(editSession);
        editSession.close();
    }
    public void debug(Player player) {
        pasteWorkingUnit(player.getLocation());
    }
    public void pasteSchem(EditSession editSession,File schem,Location center) {
        try {
            Clipboard clipboard = Objects.requireNonNull(ClipboardFormats.findByFile(schem))
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
    public void pasteSchem(File schem, Location center) {
        EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(center.getWorld()));
        pasteSchem(editSession,schem,center);
        editSession.close();
    }
}
