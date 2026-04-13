package de.realleoxian.moonlightcore.api.network;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.MoonlightCore;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
        <MSG> void bidirectional(PacketType<MSG> type, BiConsumer<PacketContext, MSG> handler);

        <MSG> void clientbound(PacketType<MSG> type, BiConsumer<PacketContext, MSG> handler);

        <MSG> void serverbound(PacketType<MSG> type, BiConsumer<PacketContext, MSG> handler);
    }

    interface PacketContext {
        void queueWork(Runnable task);

        Player player();

        Connection handler();

        PacketSender packetSender();

        EnvSide getReceptionSide();

        default void disconnect(Component message) {
            handler().disconnect(message);
        }
    }

    enum HandlerThread {
        MAIN,
        NETWORK
    }
}
