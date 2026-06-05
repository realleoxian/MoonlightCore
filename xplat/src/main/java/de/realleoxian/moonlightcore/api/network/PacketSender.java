package de.realleoxian.moonlightcore.api.network;

import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface PacketSender {
    Packet<?> createPacket(CustomPacketPayload payload);

    void sendPacket(Packet<?> packet, @Nullable PacketSendListener listener);

    default void sendPacket(CustomPacketPayload payload, @Nullable PacketSendListener listener) {
        sendPacket(createPacket(payload), listener);
    }

    default void sendPacket(CustomPacketPayload payload) {
        sendPacket(createPacket(payload), null);
    }
}
