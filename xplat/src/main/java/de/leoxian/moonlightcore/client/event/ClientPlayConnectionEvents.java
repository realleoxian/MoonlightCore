package de.leoxian.moonlightcore.client.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public final class ClientPlayConnectionEvents {
    public static final Event<Join> JOIN = Event.create(Join.class, listeners -> (packetListener, minecraft) -> {
        for (final var listener : listeners) {
            listener.onPlayJoin(packetListener, minecraft);
        }
    });
    public static final Event<Disconnect> DISCONNECT = Event.create(Disconnect.class, listeners -> (packetListener, minecraft) -> {
        for (final var listener : listeners) {
            listener.onPlayDisconnect(packetListener, minecraft);
        }
    });

    private ClientPlayConnectionEvents() {}

    @FunctionalInterface
    public interface Join {
        void onPlayJoin(ClientPacketListener packetListener, Minecraft minecraft);
    }

    @FunctionalInterface
    public interface Disconnect {
        void onPlayDisconnect(ClientPacketListener packetListener, Minecraft minecraft);
    }
}
