package de.realleoxian.moonlightcore.api.server.permission.context;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public record OfflinePermissionContext(UUID uuid) implements PermissionContext {
    @Override
    public Optional<ServerPlayer> serverPlayer() {
        return Optional.empty();
    }

    @Override
    public Optional<UUID> playerUUID() {
        return Optional.of(uuid());
    }

    @Override
    public Optional<CommandSourceStack> commandSourceStack() {
        return Optional.empty();
    }
}
