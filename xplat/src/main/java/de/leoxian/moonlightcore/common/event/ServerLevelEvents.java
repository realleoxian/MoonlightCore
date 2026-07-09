package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.server.level.ServerLevel;

public final class ServerLevelEvents {
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
    public static final Event<Save> SAVE = Event.create(Save.class, listeners -> level -> {
        for (final var listener : listeners) {
            listener.onLevelSave(level);
        }
    });

    private ServerLevelEvents() {}

    @FunctionalInterface
    public interface Load {
        void onLevelLoad(ServerLevel level);
    }

    @FunctionalInterface
    public interface Unload {
        void onLevelUnload(ServerLevel level);
    }

    @FunctionalInterface
    public interface Save {
        void onLevelSave(ServerLevel level);
    }
}
