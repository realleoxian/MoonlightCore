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
    /// Creates a packet from a packet payload
    /// @param payload the packet payload
    Packet<?> createPacket(CustomPacketPayload payload);

    /// Sends a packet
    /// @param packet The packet
    /// @param callback An optional callback to execute after the packet is sent, may be `null`
    void sendPacket(Packet<?> packet, @Nullable ChannelFutureListener callback);

    /// Sends a packet
    /// @param payload The packet payload
    /// @param callback An optional callback to execute after the packet is sent, may be `null`
    default void sendPacket(CustomPacketPayload payload, @Nullable ChannelFutureListener callback) {
        sendPacket(createPacket(payload), callback);
    }

    /// Sends a packet
    /// @param payload The packet payload
    default void sendPacket(CustomPacketPayload payload) {
        sendPacket(createPacket(payload), null);
    }

    /// Disconnects the player
    /// @param reason The reason of the disconnection
    void disconnect(Component reason);
}
