package de.leoxian.moonlightcore.common.server.permission.context;

import de.leoxian.moonlightcore.common.server.permission.PermissionContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public record OfflinePermissionContext(UUID id) implements PermissionContext {
    @Override
    public Optional<ServerPlayer> serverPlayer() {
        return Optional.empty();
    }

    @Override
    public Optional<UUID> playerId() {
        return Optional.of(id);
    }

    @Override
    public Optional<CommandSourceStack> commandSourceStack() {
        return Optional.empty();
    }
}
