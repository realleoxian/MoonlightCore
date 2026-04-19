package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.MinecraftServer;


public final class ServerTickEvents {
    public static final EventBus<Start> START = EventBus.create(Start.class, (listeners) -> (server) -> {
       for(Start listener : listeners) {
           listener.onServerTickStart(server);
       }
    });
    public static final EventBus<End> END = EventBus.create(End.class, (listeners) -> (server) -> {
        for(End listener : listeners) {
            listener.onServerTickEnd(server);
        }
    });

    private ServerTickEvents() {}

    public interface Start {
        void onServerTickStart(MinecraftServer server);
    }

    public interface End {
        void onServerTickEnd(MinecraftServer server);
    }
}
