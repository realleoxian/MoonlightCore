package de.realleoxian.moonlightcore.api.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public interface PacketSender {

    static PacketSender client() {
        return new PacketSender() {
            @Override
            public Packet<?> createPacket(ResourceLocation name, FriendlyByteBuf byteBuf) {
                return new ServerboundCustomPayloadPacket(name, byteBuf);
            }

            @Override
            public void sendPacket(Packet<?> packet) {
                ClientPacketListener listener = Minecraft.getInstance().getConnection();
                Objects.requireNonNull(listener, "Client connection its 'null', cannot send packets").send(packet);
            }
        };
    }

    static PacketSender ofPlayer(ServerPlayer player) {
        Objects.requireNonNull(player, "Cannot create packet sender for a 'null' player");

        return new PacketSender() {
            @Override
            public Packet<?> createPacket(ResourceLocation name, FriendlyByteBuf byteBuf) {
                return new ClientboundCustomPayloadPacket(name, byteBuf);
            }

            @Override
            public void sendPacket(Packet<?> packet) {
                player.connection.send(packet);
            }
        };
    }

    static PacketSender ofPlayers(Iterable<? extends ServerPlayer> players) {
        Objects.requireNonNull(players, "Player collection cannot be 'null'");

        return new PacketSender() {
            @Override
            public Packet<?> createPacket(ResourceLocation name, FriendlyByteBuf byteBuf) {
                return new ClientboundCustomPayloadPacket(name, byteBuf);
            }

            @Override
            public void sendPacket(Packet<?> packet) {
                players.forEach(p ->
                        Objects.requireNonNull(p, "Cannot send packet to 'null' player").connection.send(packet));
            }
        };
    }

    Packet<?> createPacket(ResourceLocation name, FriendlyByteBuf byteBuf);

    void sendPacket(Packet<?> packet);

    default void sendPacket(ResourceLocation name, FriendlyByteBuf byteBuf) {
        Objects.requireNonNull(name, "Packet name cannot be 'null'");
        Objects.requireNonNull(byteBuf, "Packet data cannot be 'null'");

        sendPacket(createPacket(name, byteBuf));
    }

    default void sendPacket(FriendlyByteBuf byteBuf) {
        Objects.requireNonNull(byteBuf, "Packet data cannot be 'null'");

        ResourceLocation name = byteBuf.readResourceLocation();
        sendPacket(name, byteBuf);
    }

    default <MSG> void sendPacket(PacketType<MSG> type, MSG msg) {
        Objects.requireNonNull(type, "Packet type cannot be 'null'");
        Objects.requireNonNull(msg, "Packet cannot be 'null'");

        sendPacket(type.name(), type.encode(msg));
    }

}
