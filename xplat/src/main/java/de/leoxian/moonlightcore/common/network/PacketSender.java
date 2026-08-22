package de.leoxian.moonlightcore.common.network;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface PacketSender {
    Packet<?> createPacket(CustomPacketPayload payload);

    void sendPacket(Packet<?> packet, @Nullable ChannelFutureListener callback);

    void disconnect(Component reason);

    default void sendPacket(CustomPacketPayload payload, @Nullable ChannelFutureListener callback) {
        sendPacket(createPacket(payload), callback);
    }

    default void sendPacket(CustomPacketPayload payload) {
        sendPacket(createPacket(payload), null);
    }
}
