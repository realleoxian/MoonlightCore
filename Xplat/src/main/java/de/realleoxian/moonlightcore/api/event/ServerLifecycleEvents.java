package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.MinecraftServer;

public final class ServerLifecycleEvents {
    /**
     * @see Starting#onServerStarting(MinecraftServer)
     */
    public static final EventBus<Starting> STARTING = EventBus.create(Starting.class, (listeners) -> (server) -> {
       for(ServerLifecycleEvents.Starting listener : listeners) {
           listener.onServerStarting(server);
       }
    });
    /**
     * @see Started#onServerStarted(MinecraftServer)
     */
    public static final EventBus<Started> STARTED = EventBus.create(Started.class, (listeners) -> (server) -> {
        for(ServerLifecycleEvents.Started listener : listeners) {
            listener.onServerStarted(server);
        }
    });
    /**
     * @see Stopping#onServerStopping(MinecraftServer)
     */
    public static final EventBus<Stopping> STOPPING = EventBus.create(Stopping.class, (listeners) -> (server) -> {
        for(ServerLifecycleEvents.Stopping listener : listeners) {
            listener.onServerStopping(server);
        }
    });
    /**
     * @see Stopped#onServerStopped(MinecraftServer)
     */
    public static final EventBus<Stopped> STOPPED = EventBus.create(Stopped.class, (listeners) -> (server) -> {
        for(ServerLifecycleEvents.Stopped listener : listeners) {
            listener.onServerStopped(server);
        }
    });

    private ServerLifecycleEvents() {}

    public interface Starting {
        /**
         * Invoked when a Minecraft server is starting.
         * @param server The server instance that its starting
         */
        void onServerStarting(MinecraftServer server);
    }

    public interface Started {
        /**
         * Invoked when a Minecraft server has started and is about to tick for the first time.
         * @param server The server instance that started
         */
        void onServerStarted(MinecraftServer server);
    }

    public interface Stopping {
        /**
         * Invoked when a Minecraft server has started shutting down. This occurs before the server's
         * network channel is closed and before any players are disconnected.
         * @param server The server instance that it's shutting down
         */
        void onServerStopping(MinecraftServer server);
    }

    public interface Stopped {
        /**
         * Invoked when a Minecraft server has stopped. All levels are closed and all block(entities)
         * and players have been unloaded.
         * @param server The server instance that just stopped
         */
        void onServerStopped(MinecraftServer server);
    }
}
