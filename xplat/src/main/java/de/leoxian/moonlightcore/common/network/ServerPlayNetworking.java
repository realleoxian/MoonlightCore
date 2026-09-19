package de.leoxian.moonlightcore.common.network;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ServerPlayNetworking {
    /// Registers a new Client-To-Server packet payload
    /// @param type The packet payload type
    /// @param codec The packet payload codec
    /// @param handler The handler used when the packet its received
    public static <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ServerPlayNetworking.Handler<T> handler) {
        XplatAbstraction.INSTANCE.registerPlayPayload(type, codec, handler);
    }

    /// Checks if the given player can receive a packet payload type
    /// @param player The player
    /// @param type The packet payload type
    /// @return Whether the player can receive the packet payload or not
    public static boolean canSendToPlayer(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return XplatAbstraction.INSTANCE.canSendPlayPayloadToPlayer(player, type);
    }

    /// Checks if the given player can receive a packet payload
    /// @param player The player
    /// @param payload The packet payload
    /// @return Whether the player can receive the packet payload or not
    public static boolean canSendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        return canSendToPlayer(player, payload.type());
    }

    private ServerPlayNetworking() {}

    @FunctionalInterface
    public interface Handler<T extends CustomPacketPayload> {
        /// Handles an incoming packet
        /// @param packet The packet instance
        /// @param context The network context
        void handle(T packet, Context context);
    }

    @ApiStatus.NonExtendable
    public interface Context {
        /// Enqueues a task to be executed on the main thread
        /// @param task The task
        CompletableFuture<Void> enqueueWork(Runnable task);

        /// Enqueues a task that returns a value to be executed on the main thread
        /// @param task The task
        <T> CompletableFuture<T> enqueueWork(Supplier<T> task);

        /// @return The packet listener of the player
        ServerGamePacketListenerImpl packetListener();

        /// @return The player
        ServerPlayer player();

        /// @return The Minecraft server
        MinecraftServer server();

        /// @return A packet sender to responses
        PacketSender responseSender();
    }
}
