package de.leoxian.moonlightcore.fabric.common.network;

import de.leoxian.moonlightcore.common.network.PacketSender;
import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public record FabricServerConfigurationNetworkingContext(net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.Context context) implements ServerConfigurationNetworking.Context {
    @Override
    public CompletableFuture<Void> enqueueWork(Runnable task) {
        return context.server().submit(task);
    }

    @Override
    public <T> CompletableFuture<T> enqueueWork(Supplier<T> task) {
        return context.server().submit(task);
    }

    @Override
    public ServerConfigurationPacketListenerImpl packetListener() {
        return context.packetListener();
    }

    @Override
    public PacketSender responseSender() {
        return new PacketSender() {
            @Override
            public Packet<?> createPacket(CustomPacketPayload payload) {
                return new ClientboundCustomPayloadPacket(payload);
            }

            @Override
            public void sendPacket(Packet<?> packet, @Nullable ChannelFutureListener callback) {
                packetListener().send(packet, callback);
            }

            @Override
            public void disconnect(Component reason) {
                packetListener().disconnect(reason);
            }
        };
    }
}
