package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.MinecraftServer;

public final class ServerLifecycleEvents {
    public static final EventBus<Starting> STARTING = EventBus.create(Starting.class, (listeners) -> (server) -> {
       for(ServerLifecycleEvents.Starting listener : listeners) {
           listener.onServerStarting(server);
       }
    });
    public static final EventBus<Started> STARTED = EventBus.create(Started.class, (listeners) -> (server) -> {
        for(ServerLifecycleEvents.Started listener : listeners) {
            listener.onServerStarted(server);
        }
    });
    public static final EventBus<Stopping> STOPPING = EventBus.create(Stopping.class, (listeners) -> (server) -> {
        for(ServerLifecycleEvents.Stopping listener : listeners) {
            listener.onServerStopping(server);
        }
    });
    public static final EventBus<Stopped> STOPPED = EventBus.create(Stopped.class, (listeners) -> (server) -> {
        for(ServerLifecycleEvents.Stopped listener : listeners) {
            listener.onServerStopped(server);
        }
    });

    private ServerLifecycleEvents() {}

    public interface Starting {
        void onServerStarting(MinecraftServer server);
    }

    public interface Started {
        void onServerStarted(MinecraftServer server);
    }

    public interface Stopping {
        void onServerStopping(MinecraftServer server);
    }

    public interface Stopped {
        void onServerStopped(MinecraftServer server);
    }
}
