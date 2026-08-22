package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import de.leoxian.moonlightcore.common.network.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public final class ServerPlayConnectionEvents {
    public static final Event<Join> JOIN = Event.create(Join.class, listeners -> (packetListener, sender) -> {
       for (final var listener : listeners) {
           listener.onPlayReady(packetListener, sender);
       }
    });
    public static final Event<Disconnect> DISCONNECT = Event.create(Disconnect.class, listeners -> (packetListener) -> {
       for (final var listener : listeners) {
           listener.onPlayDisconnect(packetListener);
       }
    });

    private ServerPlayConnectionEvents() {}

    @FunctionalInterface
    public interface Join {
        void onPlayReady(ServerGamePacketListenerImpl packetListener, PacketSender sender);
    }

    @FunctionalInterface
    public interface Disconnect {
        void onPlayDisconnect(ServerGamePacketListenerImpl packetListener);
    }
}
