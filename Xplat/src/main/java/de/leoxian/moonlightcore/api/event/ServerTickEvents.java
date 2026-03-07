package de.leoxian.moonlightcore.api.event;

import net.minecraft.server.MinecraftServer;

import java.util.function.BooleanSupplier;

// TODO: Improve the "hasTime" javadoc description
public final class ServerTickEvents {
    /**
     * @see Start#onServerTickStart(MinecraftServer, BooleanSupplier)
     */
    public static final EventBus<Start> START = EventBus.create((listeners) -> (server, hasTime) -> {
       for(Start listener : listeners) {
           listener.onServerTickStart(server, hasTime);
       }
    });
    /**
     * @see End#onServerTickEnd(MinecraftServer, BooleanSupplier)
     */
    public static final EventBus<End> END = EventBus.create((listeners) -> (server, hasTime) -> {
        for(End listener : listeners) {
            listener.onServerTickEnd(server, hasTime);
        }
    });

    private ServerTickEvents() {}

    public interface Start {
        /**
         * Invoked at the start of a Minecraft server tick of, before processing anything
         * @param server    The server instance that its ticking
         * @param hasTime   A boolean supplier
         */
        void onServerTickStart(MinecraftServer server, BooleanSupplier hasTime);
    }

    public interface End {
        /**
         * Invoked at the end of a Minecraft server tick, after processing it
         * @param server    The server instance that its ticking
         * @param hasTime   A boolean supplier
         */
        void onServerTickEnd(MinecraftServer server, BooleanSupplier hasTime);
    }
}
