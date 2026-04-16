package de.realleoxian.moonlightcore.api.server.permission.context;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public interface PermissionContext {
    Optional<ServerPlayer> serverPlayer();

    Optional<UUID> playerUUID();

    Optional<CommandSourceStack> commandSourceStack();
}
