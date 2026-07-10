package de.leoxian.moonlightcore.client.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Objects;

public final class ClientPacketDistributor {
    public static void sendToServer(CustomPacketPayload payload, CustomPacketPayload... payloads) {
        ClientPacketListener listener = Objects.requireNonNull(Minecraft.getInstance().getConnection());
        Objects.requireNonNull(payload, "Cannot send null payload");
        listener.send(new ServerboundCustomPayloadPacket(payload));
        for (CustomPacketPayload otherPayload : payloads) {
            Objects.requireNonNull(otherPayload, "Cannot send null payload");
            listener.send(new ServerboundCustomPayloadPacket(otherPayload));
        }
    }

    private ClientPacketDistributor() {}
}
