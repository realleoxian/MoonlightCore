package de.realleoxian.moonlightcore.api.server.permission;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.server.permission.context.CommandSourceStackContext;
import de.realleoxian.moonlightcore.api.server.permission.context.PermissionContext;
import de.realleoxian.moonlightcore.api.server.permission.context.PlayerPermissionContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Function;

public interface PermissionHelper {
    static boolean check(ServerPlayer player, ResourceLocation permission) {
        return MoonlightCore.getPermissionHelper().check(new PlayerPermissionContext(player), permission);
    }

    static boolean check(CommandSourceStack sourceStack, ResourceLocation permission) {
        return MoonlightCore.getPermissionHelper().check(new CommandSourceStackContext(sourceStack), permission);
    }

    void register(ResourceLocation permissionName, Function<PermissionContext, Boolean> resolver);

    boolean check(PermissionContext context, ResourceLocation permission);
}
