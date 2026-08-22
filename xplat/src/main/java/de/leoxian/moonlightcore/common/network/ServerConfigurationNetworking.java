package de.leoxian.moonlightcore.common.network;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ServerConfigurationNetworking {
    public static  <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, ServerConfigurationNetworking.Handler<T> handler) {
        XplatAbstraction.INSTANCE.registerConfigurationPayload(type, codec, handler);
    }

    public static boolean canSend(ServerConfigurationPacketListener packetListener, CustomPacketPayload.Type<?> type) {
        return XplatAbstraction.INSTANCE.canSendConfigurationPayload(packetListener, type);
    }

    public static boolean canSend(ServerConfigurationPacketListener packetListener, CustomPacketPayload payload) {
        return canSend(packetListener, payload.type());
    }

    public static void addTask(String modId, ServerConfigurationPacketListener packetListener, ConfigurationTask task) {
        XplatAbstraction.INSTANCE.addConfigurationTask(modId, packetListener, task);
    }

    public static void completeTask(ServerConfigurationPacketListener packetListener, ConfigurationTask.Type type) {
        XplatAbstraction.INSTANCE.completeCurrentConfigurationTask(packetListener, type);
    }

    private ServerConfigurationNetworking() {}

    @FunctionalInterface
    public interface Handler<T extends CustomPacketPayload> {
        void handle(T packet, Context context);
    }

    @ApiStatus.NonExtendable
    public interface Context {
        CompletableFuture<Void> enqueueWork(Runnable task);

        <T> CompletableFuture<T> enqueueWork(Supplier<T> task);

        ServerConfigurationPacketListenerImpl packetListener();

        PacketSender responseSender();
    }
}
