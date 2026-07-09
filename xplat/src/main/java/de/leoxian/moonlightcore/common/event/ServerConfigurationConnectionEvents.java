package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public final class ServerConfigurationConnectionEvents {
    public static final Event<Configure> BEFORE_CONFIGURE = Event.create(Configure.class, listeners -> (packetListener, server) -> {
       for (final var listener : listeners) {
           listener.onSendConfiguration(packetListener, server);
       }
    });
    public static final Event<Configure> CONFIGURE = Event.create(Configure.class, listeners -> (packetListener, server) -> {
       for (final var listener : listeners) {
           listener.onSendConfiguration(packetListener, server);
       }
    });
    public static final Event<Disconnect> DISCONNECT = Event.create(Disconnect.class, listeners -> (packetListener, server) -> {
       for (final var listener : listeners) {
           listener.onConfigureDisconnect(packetListener, server);
       }
    });

    private ServerConfigurationConnectionEvents() {}

    @FunctionalInterface
    public interface Configure {
        void onSendConfiguration(ServerConfigurationPacketListenerImpl packetListener, MinecraftServer server);
    }

    @FunctionalInterface
    public interface Disconnect {
        void onConfigureDisconnect(ServerConfigurationPacketListenerImpl packetListener, MinecraftServer server);
    }
}
