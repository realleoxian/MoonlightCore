package de.leoxian.moonlightcore.client.network;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import de.leoxian.moonlightcore.common.network.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ClientConfigurationNetworking {
    public static <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> streamCodec, Handler<T> handler) {
        XplatClientAbstraction.INSTANCE.registerConfigurationPayload(type, streamCodec, handler);
    }

    public static boolean canSend(CustomPacketPayload.Type<?> type) {
        return XplatClientAbstraction.INSTANCE.canSendPlayPayload(type);
    }

    public static boolean canSend(CustomPacketPayload payload) {
        return canSend(payload.type());
    }

    private ClientConfigurationNetworking() {}

    @FunctionalInterface
    public interface Handler<T extends CustomPacketPayload> {
        void handle(T packet, Context context);
    }

    @ApiStatus.NonExtendable
    public interface Context {
        CompletableFuture<Void> enqueueWork(Runnable task);

        <T> CompletableFuture<T> enqueueWork(Supplier<T> task);

        ClientConfigurationPacketListenerImpl packetListener();

        Minecraft minecraft();

        PacketSender responseSender();
    }
}
