package de.realleoxian.moonlightcore.api.event;

import de.realleoxian.moonlightcore.api.network.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public final class ServerPlayerNetworkEvents {
    public static final EventBus<LoggedIn> LOGGED_IN = EventBus.create(LoggedIn.class, (listeners) -> (handler, sender, server) -> {
       for(LoggedIn listener : listeners) {
           listener.onPlayerLoggedIn(handler, sender, server);
       }
    });
    public static final EventBus<LoggedOut> LOGGED_OUT = EventBus.create(LoggedOut.class, (listeners) -> (handler, server) -> {
        for(LoggedOut listener : listeners) {
            listener.onPlayerLoggedOut(handler, server);
        }
    });

    private ServerPlayerNetworkEvents() {}

    public interface LoggedIn {
        void onPlayerLoggedIn(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server);
    }

    public interface LoggedOut {
        void onPlayerLoggedOut(ServerGamePacketListenerImpl handler, MinecraftServer server);
    }
}
