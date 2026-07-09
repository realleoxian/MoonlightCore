package de.leoxian.moonlightcore.client.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.client.multiplayer.ClientLevel;

public final class ClientLevelEvents {
    public static final Event<Load> LOAD = Event.create(Load.class, listeners -> level -> {
       for (final var listener : listeners) {
           listener.onLevelLoad(level);
       }
    });
    public static final Event<Unload> UNLOAD = Event.create(Unload.class, listeners -> level -> {
        for (final var listener : listeners) {
            listener.onLevelUnload(level);
        }
    });

    private ClientLevelEvents() {}

    @FunctionalInterface
    public interface Load {
        void onLevelLoad(ClientLevel level);
    }

    @FunctionalInterface
    public interface Unload {
        void onLevelUnload(ClientLevel level);
    }
}
