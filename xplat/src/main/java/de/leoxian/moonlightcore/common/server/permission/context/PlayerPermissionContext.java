package de.leoxian.moonlightcore.common.server.permission.context;

import de.leoxian.moonlightcore.common.server.permission.PermissionContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public record PlayerPermissionContext(ServerPlayer player) implements PermissionContext {
    @Override
    public Optional<ServerPlayer> serverPlayer() {
        return Optional.of(player);
    }

    @Override
    public Optional<UUID> playerId() {
        return Optional.of(player.getUUID());
    }

    @Override
    public Optional<CommandSourceStack> commandSourceStack() {
        return Optional.of(player.createCommandSourceStack());
    }
}
