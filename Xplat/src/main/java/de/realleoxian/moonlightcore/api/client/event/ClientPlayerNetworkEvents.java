package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.EventBus;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public final class ClientPlayerNetworkEvents {
    public static final EventBus<ClientPlayerNetworkEvents.LoggedIn> LOGGED_IN = EventBus.create(LoggedIn.class, (listeners) -> (handler, sender, client) -> {
        for(ClientPlayerNetworkEvents.LoggedIn listener : listeners) {
            listener.onPlayerLoggedIn(handler, sender, client);
        }
    });
    public static final EventBus<ClientPlayerNetworkEvents.LoggedOut> LOGGED_OUT = EventBus.create(LoggedOut.class, (listeners) -> (handler, client) -> {
        for(ClientPlayerNetworkEvents.LoggedOut listener : listeners) {
            listener.onPlayerLoggedOut(handler, client);
        }
    });

    private ClientPlayerNetworkEvents() {}

    public interface LoggedIn {
        void onPlayerLoggedIn(ClientPacketListener handler, PacketSender sender, Minecraft client);
    }

    public interface LoggedOut {
        void onPlayerLoggedOut(ClientPacketListener handler, Minecraft client);
    }
}
