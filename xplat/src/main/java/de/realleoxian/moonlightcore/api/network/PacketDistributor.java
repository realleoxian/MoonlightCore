package de.realleoxian.moonlightcore.api.network;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public final class PacketDistributor {
    public static void sendToServer(CustomPacketPayload payload, CustomPacketPayload... payloads) {
        if (!MoonlightCore.getEnvironmentSide().isClient()) throw new IllegalStateException("Cannot send server-bound packet from server");
        final var networkHandler = Objects.requireNonNull(Minecraft.getInstance().getConnection());
        networkHandler.send(new ServerboundCustomPayloadPacket(payload));
        for (final var other : payloads) {
            networkHandler.send(new ServerboundCustomPayloadPacket(other));
        }
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        Objects.requireNonNull(player, "ServerPlayer may not be 'null'");
        player.connection.send(toClientboundPacket(payload, payloads));
    }

    public static void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        Objects.requireNonNull(entity, "Tracking entity may not be 'null'");

        if (entity.level().getChunkSource() instanceof ServerChunkCache chunkCache) {
            chunkCache.broadcast(entity, toClientboundPacket(payload, payloads));
        }
        throw new IllegalStateException("Cannot send client-bound packet from client");
    }

    public static void sendToPlayersTrackingEntityAndSelf(Entity entity, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        Objects.requireNonNull(entity, "Tracking entity may not be 'null'");

        if (entity.level().getChunkSource() instanceof ServerChunkCache chunkCache) {
            chunkCache.broadcastAndSend(entity, toClientboundPacket(payload, payloads));
        }
        throw new IllegalStateException("Cannot send client-bound packet from client");
    }

    public static void sendToAllPlayers(MinecraftServer server, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        Objects.requireNonNull(server, "MinecraftServer may not be 'null'");
        server.getPlayerList().broadcastAll(toClientboundPacket(payload, payloads));
    }

    public static void sendToPlayersInDimension(ServerLevel level, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        Objects.requireNonNull(level, "ServerLevel may not be 'null'");
        level.getServer().getPlayerList().broadcastAll(toClientboundPacket(payload, payloads), level.dimension());
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        Objects.requireNonNull(level, "ServerLevel may not be 'null'");
        Objects.requireNonNull(level, "ChunkPos may not be 'null'");

        final var packet = toClientboundPacket(payload, payloads);
        for (final var player : level.getChunkSource().chunkMap.getPlayers(chunkPos, false)) {
            player.connection.send(packet);
        }
    }

    public static void sendToPlayersInRadius(ServerLevel level, @Nullable ServerPlayer excluded, double x, double y, double z, double radius, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        Objects.requireNonNull(level, "ServerLevel may not be 'null'");
        level.getServer().getPlayerList().broadcast(excluded, x, y, z, radius, level.dimension(), toClientboundPacket(payload, payloads));
    }

    private static Packet<?> toClientboundPacket(CustomPacketPayload payload, CustomPacketPayload... payloads) {
        Objects.requireNonNull(payload, "Payload may not be 'null'");

        if (payloads.length > 0) {
            final var list = new ArrayList<Packet<? super ClientGamePacketListener>>();
            list.add(new ClientboundCustomPayloadPacket(payload));
            for (final var other : payloads) {
                list.add(new ClientboundCustomPayloadPacket(other));
            }
            return new ClientboundBundlePacket(list);
        }
        return new ClientboundCustomPayloadPacket(payload);
    }


    private PacketDistributor() {}
}
