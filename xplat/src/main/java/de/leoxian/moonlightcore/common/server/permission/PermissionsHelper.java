package de.leoxian.moonlightcore.common.server.permission;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.NonExtendable
public interface PermissionsHelper {
    void registerPermission(Identifier id, Function<PermissionContext, Boolean> permissionResolver);

    boolean hasPermission(ServerPlayer player, Identifier id);

    boolean hasPermission(CommandSourceStack commandSourceStack, Identifier id);
}
