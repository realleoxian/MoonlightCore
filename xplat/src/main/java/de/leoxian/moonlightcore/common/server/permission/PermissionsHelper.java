package de.leoxian.moonlightcore.common.server.permission;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.NonExtendable
public interface PermissionsHelper {
    static PermissionsHelper get() {
        return XplatAbstraction.INSTANCE.getPermissionHelper();
    }

    void registerPermission(Identifier id, Function<PermissionContext, Boolean> permissionResolver);

    boolean hasPermission(ServerPlayer player, Identifier id);

    boolean hasPermission(CommandSourceStack commandSourceStack, Identifier id);
}
