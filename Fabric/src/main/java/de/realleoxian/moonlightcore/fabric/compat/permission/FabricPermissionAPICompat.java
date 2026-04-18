package de.realleoxian.moonlightcore.fabric.compat.permission;

import de.realleoxian.moonlightcore.api.server.permission.context.CommandSourceStackContext;
import de.realleoxian.moonlightcore.api.server.permission.context.PermissionContext;
import de.realleoxian.moonlightcore.api.server.permission.context.PlayerPermissionContext;
import de.realleoxian.moonlightcore.impl.server.permission.XplatPermissionHelper;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.resources.ResourceLocation;

public class FabricPermissionAPICompat extends XplatPermissionHelper {
    @Override
    public boolean check(PermissionContext context, ResourceLocation permission) {
        if (context instanceof PlayerPermissionContext playerContext) {
            return Permissions.check(playerContext.player(), toPermissionId(permission), super.check(playerContext, permission));
        } else if (context instanceof CommandSourceStackContext commandContext) {
            return Permissions.check(commandContext.sourceStack(), toPermissionId(permission), super.check(commandContext, permission));
        }

        return false;
    }

    private static String toPermissionId(ResourceLocation permission) {
        return permission.getNamespace() + "." + permission.getPath();
    }
}
