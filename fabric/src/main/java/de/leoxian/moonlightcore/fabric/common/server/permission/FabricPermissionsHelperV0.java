package de.leoxian.moonlightcore.fabric.common.server.permission;

import de.leoxian.moonlightcore.internal.common.internal.XplatPermissionHelper;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class FabricPermissionsHelperV0 extends XplatPermissionHelper {
    private static String toPermission(Identifier id) {
        return id.getNamespace() + "." + id.getPath();
    }

    public FabricPermissionsHelperV0() {

    }

    @Override
    public boolean hasPermission(ServerPlayer player, Identifier id) {
        return Permissions.check(player, toPermission(id), super.hasPermission(player, id));
    }

    @Override
    public boolean hasPermission(CommandSourceStack commandSourceStack, Identifier id) {
        return Permissions.check(commandSourceStack, toPermission(id), super.hasPermission(commandSourceStack, id));
    }
}
