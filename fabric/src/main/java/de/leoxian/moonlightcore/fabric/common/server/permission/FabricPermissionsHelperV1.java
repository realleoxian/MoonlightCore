package de.leoxian.moonlightcore.fabric.common.server.permission;

import de.leoxian.moonlightcore.common.server.permission.PermissionContext;
import de.leoxian.moonlightcore.internal.common.internal.XplatPermissionHelper;
import net.fabricmc.fabric.api.permission.v1.PermissionNode;
import net.fabricmc.fabric.api.permission.v1.PermissionPredicates;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class FabricPermissionsHelperV1 extends XplatPermissionHelper {
    private final Map<Identifier, PermissionNode<?>> nodes = new ConcurrentHashMap<>();

    public FabricPermissionsHelperV1() {

    }

    @Override
    public void registerPermission(Identifier id, Function<PermissionContext, Boolean> permissionResolver) {
        super.registerPermission(id, permissionResolver);
        this.nodes.put(id, PermissionNode.of(id));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hasPermission(CommandSourceStack commandSourceStack, Identifier id) {
        PermissionNode<Boolean> node = (PermissionNode<Boolean>) this.nodes.get(id);
        if (node == null) {
            return false;
        }
        return PermissionPredicates.require(node, super.hasPermission(commandSourceStack, id)).test(commandSourceStack);
    }

    @Override
    public boolean hasPermission(ServerPlayer player, Identifier id) {
        PermissionNode<Boolean> node = (PermissionNode<Boolean>) this.nodes.get(id);
        if (node == null) {
            return false;
        }
        return PermissionPredicates.require(node, super.hasPermission(player, id)).test(player);
    }
}
