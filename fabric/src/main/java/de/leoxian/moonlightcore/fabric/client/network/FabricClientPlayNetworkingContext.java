package de.leoxian.moonlightcore.fabric.client.network;

import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import de.leoxian.moonlightcore.common.network.PacketSender;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public record FabricClientPlayNetworkingContext(net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context context) implements ClientPlayNetworking.Context {
    @Override
    public CompletableFuture<Void> enqueueWork(Runnable task) {
        return context.client().submit(task);
    }

    @Override
    public <T> CompletableFuture<T> enqueueWork(Supplier<T> task) {
        return context.client().submit(task);
    }

    @Override
    public ClientPacketListener packetListener() {
        return context.client().getConnection();
    }

    @Override
    public Minecraft minecraft() {
        return context.client();
    }

    @Override
    public LocalPlayer player() {
        return context.player();
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
                packetListener().getConnection().send(packet, callback);
            }

            @Override
            public void disconnect(Component reason) {
                packetListener().getConnection().disconnect(reason);
            }
        };
    }
}
