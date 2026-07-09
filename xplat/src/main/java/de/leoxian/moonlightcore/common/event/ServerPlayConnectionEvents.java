package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import de.leoxian.moonlightcore.common.network.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public final class ServerPlayConnectionEvents {
    public static final Event<Init> INIT = Event.create(Init.class, listeners -> (packetListener, server) -> {
       for (final var listener : listeners) {
           listener.onPlayInit(packetListener, server);
       }
    });
    public static final Event<Join> JOIN = Event.create(Join.class, listeners -> (packetListener, sender, server) -> {
       for (final var listener : listeners) {
           listener.onPlayReady(packetListener, sender, server);
       }
    });
    public static final Event<Disconnect> DISCONNECT = Event.create(Disconnect.class, listeners -> (packetListener, server) -> {
       for (final var listener : listeners) {
           listener.onPlayDisconnect(packetListener, server);
       }
    });

    private ServerPlayConnectionEvents() {}

    @FunctionalInterface
    public interface Init {
        void onPlayInit(ServerGamePacketListenerImpl packetListener, MinecraftServer server);
    }

    @FunctionalInterface
    public interface Join {
        void onPlayReady(ServerGamePacketListenerImpl packetListener, PacketSender sender, MinecraftServer server);
    }

    @FunctionalInterface
    public interface Disconnect {
        void onPlayDisconnect(ServerGamePacketListenerImpl packetListener, MinecraftServer server);
    }
}
