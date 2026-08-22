package de.leoxian.moonlightcore.internal.common.internal;

import de.leoxian.moonlightcore.common.server.permission.PermissionContext;
import de.leoxian.moonlightcore.common.server.permission.PermissionsHelper;
import de.leoxian.moonlightcore.common.server.permission.context.CommandPermissionContext;
import de.leoxian.moonlightcore.common.server.permission.context.PlayerPermissionContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class XplatPermissionHelper implements PermissionsHelper {
    private final Map<Identifier, Function<PermissionContext, Boolean>> resolvers = new ConcurrentHashMap<>();

    @Override
    public void registerPermission(Identifier id, Function<PermissionContext, Boolean> permissionResolver) {
        this.resolvers.put(id, permissionResolver);
    }

    @Override
    public boolean hasPermission(ServerPlayer player, Identifier id) {
        final Function<PermissionContext, Boolean> resolver = this.resolvers.get(id);
        if (resolver == null) {
            return false;
        }
        return resolver.apply(new PlayerPermissionContext(player));
    }

    @Override
    public boolean hasPermission(CommandSourceStack commandSourceStack, Identifier id) {
        final Function<PermissionContext, Boolean> resolver = this.resolvers.get(id);
        if (resolver == null) {
            return false;
        }
        return resolver.apply(new CommandPermissionContext(commandSourceStack));
    }
}
