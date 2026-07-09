package de.leoxian.moonlightcore.common.server.permission.context;

import de.leoxian.moonlightcore.common.server.permission.PermissionContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public record CommandPermissionContext(CommandSourceStack sourceStack) implements PermissionContext {
    @Override
    public Optional<ServerPlayer> serverPlayer() {
        return Optional.ofNullable(sourceStack.getPlayer());
    }

    @Override
    public Optional<UUID> playerId() {
        return Optional.ofNullable(sourceStack.getPlayer()).map(ServerPlayer::getUUID);
    }

    @Override
    public Optional<CommandSourceStack> commandSourceStack() {
        return Optional.of(sourceStack);
    }
}
