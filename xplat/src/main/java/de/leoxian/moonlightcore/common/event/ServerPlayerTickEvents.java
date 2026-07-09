package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.server.level.ServerPlayer;

public final class ServerPlayerTickEvents {
    public static final Event<Start> START = Event.create(Start.class, listeners -> player -> {
       for (final var listener : listeners) {
           listener.onServerPlayerTickStart(player);
       }
    });
    public static final Event<End> END = Event.create(End.class, listeners -> player -> {
        for (final var listener : listeners) {
            listener.onServerPlayerTickEnd(player);
        }
    });

    private ServerPlayerTickEvents() {}

    @FunctionalInterface
    public interface Start {
        void onServerPlayerTickStart(ServerPlayer player);
    }

    @FunctionalInterface
    public interface End {
        void onServerPlayerTickEnd(ServerPlayer player);
    }
}
