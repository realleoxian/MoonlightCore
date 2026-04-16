package de.realleoxian.moonlightcore.impl.server.permission;

import de.realleoxian.moonlightcore.api.server.permission.PermissionHelper;
import de.realleoxian.moonlightcore.api.server.permission.context.PermissionContext;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class XplatPermissionHelper implements PermissionHelper {
    private final Map<ResourceLocation, Function<PermissionContext, Boolean>> resolvers = new HashMap<>();

    @Override
    public void register(ResourceLocation permission, Function<PermissionContext, Boolean> resolver) {
        this.resolvers.put(permission, resolver);
    }

    @Override
    public boolean check(PermissionContext context, ResourceLocation permission) {
        Function<PermissionContext, Boolean> resolver = this.resolvers.get(permission);
        if (resolver == null) {
            return false;
        }

        return resolver.apply(context);
    }
}
