package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public final class ServerPlayerNetworkEvents {
    /**
     * @see LoggedIn#onPlayerLoggedIn(MinecraftServer, ServerPlayer, ServerGamePacketListenerImpl)
     */
    public static final EventBus<LoggedIn> LOGGED_IN = EventBus.create(LoggedIn.class, (listeners) -> (server, player, handler) -> {
       for(LoggedIn listener : listeners) {
           listener.onPlayerLoggedIn(server, player, handler);
       }
    });
    /**
     * @see LoggedOut#onPlayerLoggedOut(MinecraftServer, ServerPlayer, ServerGamePacketListenerImpl)
     */
    public static final EventBus<LoggedOut> LOGGED_OUT = EventBus.create(LoggedOut.class, (listeners) -> (server, player, handler) -> {
        for(LoggedOut listener : listeners) {
            listener.onPlayerLoggedOut(server, player, handler);
        }
    });

    private ServerPlayerNetworkEvents() {}

    public interface LoggedIn {
        /**
         * Invoked when a player logs into a Minecraft server
         * @param server The server the player logged in
         * @param player The player that logged in
         */
        void onPlayerLoggedIn(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler);
    }

    public interface LoggedOut {
        /**
         * Invoked when a player logged out from a Minecraft server
         * @param server The server the player logged out from
         * @param player The player that logged out
         */
        void onPlayerLoggedOut(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler);
    }
}
