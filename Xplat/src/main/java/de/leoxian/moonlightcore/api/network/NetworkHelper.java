package de.leoxian.moonlightcore.api.network;

import de.leoxian.moonlightcore.api.EnvSide;
import de.leoxian.moonlightcore.api.runtime.MoonlightCoreRuntime;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public interface NetworkHelper {
    String DEFAULT_NETWORK_VERSION = "1";

    static NetworkHelper get() {
        return MoonlightCoreRuntime.RUNTIME.getNetworkHelper();
    }

    NetworkHelper clientOnly(String namespace);

    NetworkHelper serverOnly(String namespace);

    PacketRegistrar registrar(String namespace, String networkVersion, HandlerThread handlerThread);

    <MSG> void registerPacketReceiver(PacketType<MSG> type, BiConsumer<MSG, PacketContext> receiver);

    <MSG> void sendTo(PacketSender sender, MSG packet);

    <MSG> void sendToServer(MSG packet);

    <MSG> void sendToPlayer(ServerPlayer player, MSG packet);

    <MSG> void sendToPlayers(Iterable<? extends ServerPlayer> players, MSG packet);

    boolean canServerReceive(ResourceLocation packet);

    boolean canPlayerReceive(Player player, ResourceLocation packet);

    default PacketRegistrar registrar(String namespace, String networkVersion) {
        return registrar(namespace, networkVersion, HandlerThread.MAIN);
    }

    default PacketRegistrar registrar(String namespace, HandlerThread handlerThread) {
        return registrar(namespace, DEFAULT_NETWORK_VERSION, handlerThread);
    }

    default PacketRegistrar registrar(String namespace) {
        return registrar(namespace, DEFAULT_NETWORK_VERSION, HandlerThread.NETWORK);
    }

    interface PacketRegistrar {

        <MSG> void bidirectional(PacketType<MSG> type, BiConsumer<PacketContext, MSG> context);

        <MSG> void clientbound(PacketType<MSG> type, BiConsumer<PacketContext, MSG> context);

        <MSG> void serverbound(PacketType<MSG> type, BiConsumer<PacketContext, MSG> context);

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
