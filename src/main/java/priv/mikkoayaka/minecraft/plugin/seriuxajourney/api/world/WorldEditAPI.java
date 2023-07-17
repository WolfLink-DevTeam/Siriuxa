package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.world;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
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

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Singleton
public class WorldEditAPI {
    File schemFolder;

    public WorldEditAPI() {
        schemFolder = new File(Framework.getInstance().getDataFolder(), "seriuxajourney_schematic");
        if (!schemFolder.exists()) {
            schemFolder.mkdirs();
        }
    }

    public List<File> getWorkingUnitSchemFiles() {
        return getSchemFilesByPrefix("working_unit");
    }

    public List<File> getSchemFilesByPrefix(String prefix) {
        List<File> schemFiles = new ArrayList<>();
        File[] subFiles = schemFolder.listFiles();
        if (subFiles == null) return schemFiles;
        for (File schemFile : subFiles) {
            if (schemFile.getName().startsWith(prefix)) {
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
    public void pasteWorkingUnit(LocationCommandSender locationCommandSender) {
        List<File> workingUnitFiles = getWorkingUnitSchemFiles();
        if (workingUnitFiles.size() == 0) {
            Notifier.error("没有找到可用的工作单元结构");
            return;
        }
        File schem = workingUnitFiles.get((int) (workingUnitFiles.size() * Math.random()));
        pasteSchem(schem, locationCommandSender, false);
    }

    @Nullable
    public EditSession pasteEvacuationUnit(LocationCommandSender locationCommandSender) {
        List<File> evacuationUnitFiles = getEvacuationUnitSchemFiles();
        if (evacuationUnitFiles.size() == 0) {
            Notifier.error("没有找到可用的撤离单元结构");
            return null;
        }
        File schem = evacuationUnitFiles.get((int) (evacuationUnitFiles.size() * Math.random()));
        return pasteSchem(schem, locationCommandSender, true);
    }

    public void pasteSchem(EditSession editSession, File schem, Location center) {
        try {
            Clipboard clipboard = Objects.requireNonNull(ClipboardFormats.findByFile(schem))
                    .getReader(new FileInputStream(schem))
                    .read();
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(center.getBlockX(), center.getBlockY(), center.getBlockZ()))
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
        } catch (Exception e) {
            e.printStackTrace();
            Notifier.error("在拷贝结构时出现异常，相关结构文件：" + schem.getName());
        }
        Notifier.debug("在" + Objects.requireNonNull(center.getWorld()).getName() + " " + center.getBlockX() + "|" + center.getBlockY() + "|" + center.getBlockZ() + "生成了一个结构：" + schem.getName());
    }

    /**
     * 如果不需要撤销该操作，则会返回空值
     */
    @Nullable
    public EditSession pasteSchem(File schem, LocationCommandSender locationCommandSender, boolean needUndo) {
        Location center = locationCommandSender.getLocation();
        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .actor(BukkitAdapter.adapt(locationCommandSender))
                .world(BukkitAdapter.adapt(center.getWorld()))
                .build();
        pasteSchem(editSession, schem, center);
        if (needUndo) {
            locationCommandSender.getLocalSession().remember(editSession);
            return editSession;
        } else {
            editSession.close();
            return null;
        }
    }

    public void undoPaste(LocationCommandSender locationCommandSender, EditSession editSession) {
        LocalSession localSession = locationCommandSender.getLocalSession();
        if (localSession == null) {
            Notifier.error("LocalSession 为空！");
            return;
        }
        EditSession es = localSession.undo(null, editSession.getActor());
        Notifier.debug("撤销了一个" + editSession.getWorld().getName() + "世界生成的结构");
        es.close();
    }
}
