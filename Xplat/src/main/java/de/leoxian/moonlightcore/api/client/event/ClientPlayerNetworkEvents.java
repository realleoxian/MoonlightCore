package de.leoxian.moonlightcore.api.client.event;

import de.leoxian.moonlightcore.api.event.EventBus;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

public final class ClientPlayerNetworkEvents {
    /**
     * @see LoggedIn#onPlayerLoggedIn(LocalPlayer, ClientPacketListener)
     */
    public static final EventBus<ClientPlayerNetworkEvents.LoggedIn> LOGGED_IN = EventBus.create((listeners) -> (player, handler) -> {
        for(ClientPlayerNetworkEvents.LoggedIn listener : listeners) {
            listener.onPlayerLoggedIn(player, handler);
        }
    });
    /**
     * @see LoggedOut#onPlayerLoggedOut(LocalPlayer, ClientPacketListener)
     */
    public static final EventBus<ClientPlayerNetworkEvents.LoggedOut> LOGGED_OUT = EventBus.create((listeners) -> (player, handler) -> {
        for(ClientPlayerNetworkEvents.LoggedOut listener : listeners) {
            listener.onPlayerLoggedOut(player, handler);
        }
    });

    private ClientPlayerNetworkEvents() {}

    public interface LoggedIn {
        /**
         * Invoked when a player logs into a Minecraft server
         * @param player    The player that logged into the server
         * @param handler   The client connection handler
         */
        void onPlayerLoggedIn(LocalPlayer player, ClientPacketListener handler);
    }

    public interface LoggedOut {
        /**
         * Invoked when a player logs out from a Minecraft server
         * @param player    The player that logged out from the server
         * @param handler   The client connection handler
         */
        void onPlayerLoggedOut(LocalPlayer player, ClientPacketListener handler);
    }
}
