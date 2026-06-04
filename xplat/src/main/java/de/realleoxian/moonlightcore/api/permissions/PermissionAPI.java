package de.realleoxian.moonlightcore.api.permissions;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public interface PermissionAPI {
    static PermissionAPI get() {
        return MoonlightCore.RUNTIME.getPermissionAPI();
    }

    <T> PermissionNode<T> register(ResourceLocation name, Class<T> permissionType, PermissionResolver<T> resolver);

    <T> T getPermission(PermissionNode<T> node, ServerPlayer player);

    <T> T getPermission(PermissionNode<T> node, UUID entityId);

    <T> T getPermission(PermissionNode<T> node, CommandSourceStack commandSourceStack);
}
