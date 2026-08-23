package de.leoxian.moonlightcore.fabric.common.network;

import de.leoxian.moonlightcore.common.network.PacketSender;
import de.leoxian.moonlightcore.common.network.ServerPlayNetworking;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public record FabricServerPlayNetworkingContext(net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context context) implements ServerPlayNetworking.Context {
    @Override
    public CompletableFuture<Void> enqueueWork(Runnable task) {
        return context.server().submit(task);
    }

    @Override
    public <T> CompletableFuture<T> enqueueWork(Supplier<T> task) {
        return context.server().submit(task);
    }

    @Override
    public ServerGamePacketListenerImpl packetListener() {
        return context.player().connection;
    }

    @Override
    public ServerPlayer player() {
        return context.player();
    }

    @Override
    public MinecraftServer server() {
        return context.server();
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
