package org.wolflink.minecraft.plugin.siriuxa.api.world;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Getter
public class LocationCommandSender implements BlockCommandSender {

    private final Location location;
    @Getter
    private final LocalSession localSession;
    private final UUID uuid = UUID.randomUUID();

    public LocationCommandSender(Location center) {
        location = center;
        World world = center.getWorld();
        localSession = new LocalSession(WorldEdit.getInstance().getConfiguration());
        localSession.setWorldOverride(BukkitAdapter.adapt(world));
    }

    @NotNull
    @Override
    public Block getBlock() {
        return location.getBlock();
    }

    @Override
    public void sendMessage(@NotNull String s) {
        // do nothing
    }

    @Override
    public void sendMessage(@NotNull String... strings) {
        // do nothing
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String s) {
        // do nothing
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {
        // do nothing
    }

    @NotNull
    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @NotNull
    @Override
    public String getName() {
        return uuid.toString();
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return new CommandSender.Spigot();
    }

    @Override
    public @NotNull Component name() {
        return Component.text("LocationCommandSender");
    }

    @Override
    public boolean isPermissionSet(@NotNull String s) {
        return true;
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return true;
    }

    @Override
    public boolean hasPermission(@NotNull String s) {
        return true;
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return true;
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return null;
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
        return null;
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return null;
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
        // do nothing
    }

    @Override
    public void recalculatePermissions() {
        // do nothing
    }

    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Collections.emptySet();
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean b) {
        // do nothing
    }
}
