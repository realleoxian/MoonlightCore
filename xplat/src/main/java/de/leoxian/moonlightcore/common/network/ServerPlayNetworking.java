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
    public static <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ServerPlayNetworking.Handler<T> handler) {
        XplatAbstraction.INSTANCE.registerPlayPayload(type, codec, handler);
    }

    public static boolean canSendToPlayer(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return XplatAbstraction.INSTANCE.canSendPlayPayloadToPlayer(player, type);
    }

    public static boolean canSendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        return canSendToPlayer(player, payload.type());
    }

    private ServerPlayNetworking() {}

    @FunctionalInterface
    public interface Handler<T extends CustomPacketPayload> {
        void handle(T packet, Context context);
    }

    @ApiStatus.NonExtendable
    public interface Context {
        CompletableFuture<Void> enqueueWork(Runnable task);

        <T> CompletableFuture<T> enqueueWork(Supplier<T> task);

        ServerGamePacketListenerImpl packetListener();

        ServerPlayer player();

        MinecraftServer server();

        PacketSender responseSender();
    }
}
