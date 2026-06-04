package de.realleoxian.moonlightcore.api.permissions;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public interface PermissionContext {
    Optional<ServerPlayer> player();

    Optional<UUID> entityId();

    Optional<CommandSourceStack> commandSourceStack();

    Type type();

    enum Type {
        PLAYER,
        ENTITY,
        SYSTEM
    }
}
