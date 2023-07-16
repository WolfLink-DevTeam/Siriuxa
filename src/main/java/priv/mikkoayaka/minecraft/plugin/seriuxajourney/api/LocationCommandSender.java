package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class LocationCommandSender implements BlockCommandSender {

    @Getter
    private final Location location;
    private final UUID uuid = UUID.randomUUID();
    public LocationCommandSender(Location center) {
        location = center;
    }

    @NotNull
    @Override
    public Block getBlock() {
        return location.getBlock();
    }

    @Override
    public void sendMessage(@NotNull String s) {
    }

    @Override
    public void sendMessage(@NotNull String... strings) {
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String s) {
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {
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

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
        return null;
    }

    @NotNull
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
    }

    @Override
    public void recalculatePermissions() {
    }

    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean b) {
    }
}
