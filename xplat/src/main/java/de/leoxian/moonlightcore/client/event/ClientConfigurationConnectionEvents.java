package de.leoxian.moonlightcore.client.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;

public final class ClientConfigurationConnectionEvents {
    public static final Event<Init> INIT = Event.create(Init.class, listeners -> (packetListener, minecraft) -> {
       for (final var listener : listeners) {
           listener.onConfigurationInit(packetListener, minecraft);
       }
    });
    public static final Event<Start> START = Event.create(Start.class, listeners -> (packetListener, minecraft) -> {
        for (final var listener : listeners) {
            listener.onConfigurationStart(packetListener, minecraft);
        }
    });
    public static final Event<Complete> COMPLETE = Event.create(Complete.class, listeners -> (packetListener, minecraft) -> {
        for (final var listener : listeners) {
            listener.onConfigurationComplete(packetListener, minecraft);
        }
    });
    public static final Event<Disconnect> DISCONNECT = Event.create(Disconnect.class, listeners -> (packetListener, minecraft) -> {
        for (final var listener : listeners) {
            listener.onConfigurationDisconnect(packetListener, minecraft);
        }
    });

    private ClientConfigurationConnectionEvents() {}

    @FunctionalInterface
    public interface Init {
        void onConfigurationInit(ClientConfigurationPacketListenerImpl packetListener, Minecraft minecraft);
    }

    @FunctionalInterface
    public interface Start {
        void onConfigurationStart(ClientConfigurationPacketListenerImpl packetListener, Minecraft minecraft);
    }

    @FunctionalInterface
    public interface Complete {
        void onConfigurationComplete(ClientConfigurationPacketListenerImpl packetListener, Minecraft minecraft);
    }

    @FunctionalInterface
    public interface Disconnect {
        void onConfigurationDisconnect(ClientConfigurationPacketListenerImpl packetListener, Minecraft minecraft);
    }
}
