package de.leoxian.moonlightcore.neoforge.client.network;

import de.leoxian.moonlightcore.client.network.ClientConfigurationNetworking;
import de.leoxian.moonlightcore.common.network.PacketSender;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public record NeoforgeClientConfigurationNetworkingContext(IPayloadContext context) implements ClientConfigurationNetworking.Context {
    @Override
    public CompletableFuture<Void> enqueueWork(Runnable task) {
        return context.enqueueWork(task);
    }

    @Override
    public <T> CompletableFuture<T> enqueueWork(Supplier<T> task) {
        return context.enqueueWork(task);
    }

    @Override
    public ClientConfigurationPacketListenerImpl packetListener() {
        if (context.listener() instanceof ClientConfigurationPacketListenerImpl listener) {
            return listener;
        }
        return null;
    }

    @Override
    public Minecraft minecraft() {
        return Minecraft.getInstance();
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
                context.connection().send(packet, callback);
            }

            @Override
            public void disconnect(Component reason) {
                context.connection().disconnect(reason);
            }
        };
    }
}
