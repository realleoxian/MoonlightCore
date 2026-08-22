package de.leoxian.moonlightcore.client.network;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import de.leoxian.moonlightcore.common.network.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class ClientPlayNetworking {
    public static <MSG extends CustomPacketPayload> void register(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec, Handler<MSG> handler) {
        XplatClientAbstraction.INSTANCE.registerPlayPayload(type, streamCodec, handler);
    }

    public static boolean canSend(CustomPacketPayload.Type<?> type) {
        return XplatClientAbstraction.INSTANCE.canSendPlayPayload(type);
    }

    public static boolean canSend(CustomPacketPayload payload) {
        return canSend(payload.type());
    }

    private ClientPlayNetworking() {}

    @FunctionalInterface
    public interface Handler<T extends CustomPacketPayload> {
        void handle(T packet, Context context);
    }

    @ApiStatus.NonExtendable
    public interface Context {
        CompletableFuture<Void> enqueueWork(Runnable task);

        <T> CompletableFuture<T> enqueueWork(Supplier<T> task);

        ClientPacketListener packetListener();

        Minecraft minecraft();

        LocalPlayer player();

        PacketSender responseSender();
    }
}
