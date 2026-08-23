package de.leoxian.moonlightcore.fabric.client.network;

import de.leoxian.moonlightcore.client.network.ClientConfigurationNetworking;
import de.leoxian.moonlightcore.common.network.PacketSender;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public record FabricClientConfigurationNetworkingContext(net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.Context context) implements ClientConfigurationNetworking.Context {
    @Override
    public CompletableFuture<Void> enqueueWork(Runnable task) {
        return context.client().submit(task);
    }

    @Override
    public <T> CompletableFuture<T> enqueueWork(Supplier<T> task) {
        return context.client().submit(task);
    }

    @Override
    public ClientConfigurationPacketListenerImpl packetListener() {
        return context.packetListener();
    }

    @Override
    public Minecraft minecraft() {
        return context.client();
    }

    @Override
    public PacketSender responseSender() {
        return new PacketSender() {
            @Override
            public Packet<?> createPacket(CustomPacketPayload payload) {
                return new ServerboundCustomPayloadPacket(payload);
            }

            @Override
            public void sendPacket(Packet<?> packet, @Nullable ChannelFutureListener callback) {
                packetListener().send(packet);
            }

            @Override
            public void disconnect(Component reason) {
                // no-op
            }
        };
    }
}
