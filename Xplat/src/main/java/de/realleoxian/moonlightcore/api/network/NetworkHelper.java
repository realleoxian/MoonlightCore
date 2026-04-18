package de.realleoxian.moonlightcore.api.network;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.MoonlightCore;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public interface NetworkHelper {
    static NetworkHelper get() {
        return MoonlightCore.getNetworkHelper();
    }

    NetworkHelper clientOnly(String namespace);

    NetworkHelper serverOnly(String namespace);

    NetworkHelper protocolVersion(String namespace, String protocolVersion);

    NetworkHelper handlerThread(String namespace, HandlerThread handlerThread);

    PacketRegistrar registrar(String namespace);

    <MSG> void sendToServer(MSG packet);

    <MSG> void sendToPlayer(ServerPlayer player, MSG packet);

    default <MSG> void sendToPlayers(Iterable<? extends ServerPlayer> players, MSG packet) {
        players.forEach(p -> sendToPlayer(p, packet));
    }

    boolean canServerReceive(ResourceLocation packet);

    boolean canPlayerReceive(ServerPlayer player, ResourceLocation packet);

    interface PacketRegistrar {
        <MSG> void clientbound(PacketType<MSG> type, BiConsumer<MSG, PacketContext<ClientPacketListener>> handler);

        default <MSG> void clientbound(ResourceLocation name, Class<MSG> msgClass, PacketEncoder<FriendlyByteBuf, MSG> encoder, PacketDecoder<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, PacketContext<ClientPacketListener>> handler) {
            clientbound(new PacketType<>(name, msgClass, encoder, decoder), handler);
        }

        <MSG> void serverbound(PacketType<MSG> type, BiConsumer<MSG, PacketContext<ServerGamePacketListenerImpl>> handler);

        default <MSG> void serverbound(ResourceLocation name, Class<MSG> msgClass, PacketEncoder<FriendlyByteBuf, MSG> encoder, PacketDecoder<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, PacketContext<ServerGamePacketListenerImpl>> handler) {
            serverbound(new PacketType<>(name, msgClass, encoder, decoder), handler);
        }
    }

    interface PacketContext<H extends PacketListener> {
        void queueWork(Runnable task);

        Player player();

        H handler();

        Connection connection();

        PacketSender packetSender();

        EnvSide getReceptionSide();

        default void disconnect(Component message) {
            connection().disconnect(message);
        }
    }

    enum HandlerThread {
        MAIN,
        NETWORK
    }
}
