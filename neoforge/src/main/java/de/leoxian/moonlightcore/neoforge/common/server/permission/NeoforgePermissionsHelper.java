package de.leoxian.moonlightcore.neoforge.common.server.permission;

import de.leoxian.moonlightcore.common.server.permission.PermissionContext;
import de.leoxian.moonlightcore.common.server.permission.PermissionsHelper;
import de.leoxian.moonlightcore.common.server.permission.context.OfflinePermissionContext;
import de.leoxian.moonlightcore.common.server.permission.context.PlayerPermissionContext;
import de.leoxian.moonlightcore.internal.common.internal.XplatPermissionHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class NeoforgePermissionsHelper extends XplatPermissionHelper {
    private final Map<Identifier, PermissionNode<?>> nodes = new ConcurrentHashMap<>();

    public NeoforgePermissionsHelper() {
        NeoForge.EVENT_BUS.addListener(this::registerNodes);
    }

    private void registerNodes(PermissionGatherEvent.Nodes event) {
        event.addNodes(this.nodes.values());
    }

    @Override
    public void registerPermission(Identifier id, Function<PermissionContext, Boolean> permissionResolver) {
        super.registerPermission(id, permissionResolver);
        this.nodes.put(id, new PermissionNode<>(id, PermissionTypes.BOOLEAN, (sp, uuid, _) ->
                permissionResolver.apply(sp != null ? new PlayerPermissionContext(sp) : new OfflinePermissionContext(uuid))));
    }

    @Override
    public boolean hasPermission(ServerPlayer player, Identifier id) {
        final PermissionNode<Boolean> node = (PermissionNode<Boolean>) this.nodes.get(id);
        if (node == null) {
            return false;
        }
        return PermissionAPI.getPermission(player, node);
    }

    @Override
    public boolean hasPermission(CommandSourceStack commandSourceStack, Identifier id) {
        final PermissionNode<Boolean> node = (PermissionNode<Boolean>) this.nodes.get(id);
        if (node == null) {
            return false;
        }

        final var player = commandSourceStack.getPlayer();
        return player != null ? PermissionAPI.getPermission(player, node) : super.hasPermission(commandSourceStack, id);
    }
}
