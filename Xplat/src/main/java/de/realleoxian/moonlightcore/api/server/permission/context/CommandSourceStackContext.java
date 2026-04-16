package de.realleoxian.moonlightcore.api.server.permission.context;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public record CommandSourceStackContext(CommandSourceStack sourceStack) implements PermissionContext {
    @Override
    public Optional<ServerPlayer> serverPlayer() {
        return Optional.ofNullable(sourceStack.getPlayer());
    }

    @Override
    public Optional<UUID> playerUUID() {
        return serverPlayer().map(Entity::getUUID);
    }

    @Override
    public Optional<CommandSourceStack> commandSourceStack() {
        return Optional.of(sourceStack);
    }
}
