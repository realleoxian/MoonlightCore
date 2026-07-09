package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.server.MinecraftServer;

public final class ServerTickEvents {
    public static final Event<ServerTickEvents.Start> START = Event.create(ServerTickEvents.Start.class, listeners -> server -> {
       for (final var listener : listeners) {
           listener.onServerTickStart(server);
       }
    });
    public static final Event<ServerTickEvents.End> END = Event.create(ServerTickEvents.End.class, listeners -> server -> {
        for (final var listener : listeners) {
            listener.onServerTickEnd(server);
        }
    });

    private ServerTickEvents() {}

    @FunctionalInterface
    public interface Start {
        void onServerTickStart(MinecraftServer server);
    }

    @FunctionalInterface
    public interface End {
        void onServerTickEnd(MinecraftServer server);
    }
}
