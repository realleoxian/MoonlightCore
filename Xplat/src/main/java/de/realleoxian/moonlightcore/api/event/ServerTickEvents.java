package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.MinecraftServer;

import java.util.function.BooleanSupplier;

public final class ServerTickEvents {
    public static final EventBus<Start> START = EventBus.create(Start.class, (listeners) -> (server, hasTime) -> {
       for(Start listener : listeners) {
           listener.onServerTickStart(server, hasTime);
       }
    });
    public static final EventBus<End> END = EventBus.create(End.class, (listeners) -> (server, hasTime) -> {
        for(End listener : listeners) {
            listener.onServerTickEnd(server, hasTime);
        }
    });

    private ServerTickEvents() {}

    public interface Start {
        void onServerTickStart(MinecraftServer server, BooleanSupplier hasTime);
    }

    public interface End {
        void onServerTickEnd(MinecraftServer server, BooleanSupplier hasTime);
    }
}
