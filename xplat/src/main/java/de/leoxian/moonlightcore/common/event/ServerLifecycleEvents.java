package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.server.MinecraftServer;

public final class ServerLifecycleEvents {
    public static final Event<Starting> STARTING = Event.create(Starting.class, listeners -> server -> {
        for (final var listener : listeners) {
            listener.onServerStarting(server);
        }
    });
    public static final Event<Started> STARTED = Event.create(Started.class, listeners -> server -> {
        for (final var listener : listeners) {
            listener.onServerStarted(server);
        }
    });
    public static final Event<Stopping> STOPPING = Event.create(Stopping.class, listeners -> server -> {
        for (final var listener : listeners) {
            listener.onServerStopping(server);
        }
    });
    public static final Event<Stopped> STOPPED = Event.create(Stopped.class, listeners -> server -> {
        for (final var listener : listeners) {
            listener.onServerStopped(server);
        }
    });

    private ServerLifecycleEvents() {}

    @FunctionalInterface
    public interface Starting {
        void onServerStarting(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Started {
        void onServerStarted(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Stopping {
        void onServerStopping(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Stopped {
        void onServerStopped(MinecraftServer server);
    }
}
