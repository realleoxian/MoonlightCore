package de.realleoxian.moonlightcore.forge.server.permission;

import de.realleoxian.moonlightcore.api.server.permission.context.CommandSourceStackContext;
import de.realleoxian.moonlightcore.api.server.permission.context.OfflinePermissionContext;
import de.realleoxian.moonlightcore.api.server.permission.context.PermissionContext;
import de.realleoxian.moonlightcore.api.server.permission.context.PlayerPermissionContext;
import de.realleoxian.moonlightcore.impl.server.permission.XplatPermissionHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ForgePermissionHelperImpl extends XplatPermissionHelper {
    private final Map<ResourceLocation, PermissionNode<?>> nodes = new HashMap<>();

    public ForgePermissionHelperImpl() {
        MinecraftForge.EVENT_BUS.addListener((PermissionGatherEvent.Nodes event) -> event.addNodes(this.nodes.values()));
    }

    @Override
    public void register(ResourceLocation permission, Function<PermissionContext, Boolean> resolver) {
        super.register(permission, resolver);
        this.nodes.put(permission, new PermissionNode<Boolean>(permission, PermissionTypes.BOOLEAN, (serverPlayer, uuid, permissionDynamicContext) ->
                resolver.apply(serverPlayer == null ? new OfflinePermissionContext(uuid) : new PlayerPermissionContext(serverPlayer))));
    }

    @Override
    public boolean check(PermissionContext context, ResourceLocation permission) {
        @SuppressWarnings("unchecked")
        PermissionNode<Boolean> node = (PermissionNode<Boolean>) this.nodes.get(permission);
        if (node == null) {
            return false;
        }

        if (context instanceof PlayerPermissionContext playerContext){
            return PermissionAPI.getPermission(playerContext.player(), node);
        } else if (context instanceof CommandSourceStackContext commandContext) {
            ServerPlayer player = commandContext.commandSourceStack().get().getPlayer();
            return player == null ? super.check(commandContext, permission) : PermissionAPI.getPermission(player, node);
        }

        return false;
    }
}
