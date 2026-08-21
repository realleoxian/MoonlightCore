package de.leoxian.moonlightcore.common.server.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

@FunctionalInterface
public interface PlayerRemover {
    PlayerRemover DEFAULT = (server, player) -> {
        player.sendSystemMessage(Component.translatable("moonlightcore.message.dimension.remove.deleted", player.level().dimension().identifier()));
        ServerLevel level = server.getLevel(player.level().getRespawnData().dimension());
        if (level != null && level != player.level()) {
            BlockPos pos = player.getRespawnConfig().respawnData().pos();
            player.teleportTo(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot(), false);
        } else {
            level = server.overworld();
            BlockPos pos = level.getRespawnData().pos();
            player.teleportTo(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot(), false);
        }
        player.setDeltaMovement(0.0, 0.0, 0.0);
    };

    void removePlayer(final MinecraftServer server, final ServerPlayer player);
}
