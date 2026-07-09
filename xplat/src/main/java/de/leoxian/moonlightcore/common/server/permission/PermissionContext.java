package de.leoxian.moonlightcore.common.server.permission;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.UUID;

@ApiStatus.NonExtendable
public interface PermissionContext {
    Optional<ServerPlayer> serverPlayer();

    Optional<UUID> playerId();

    Optional<CommandSourceStack> commandSourceStack();
}
