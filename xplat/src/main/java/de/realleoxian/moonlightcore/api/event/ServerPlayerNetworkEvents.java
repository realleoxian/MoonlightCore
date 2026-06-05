package de.realleoxian.moonlightcore.api.event;

import de.realleoxian.moonlightcore.api.network.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;

public final class ServerPlayerNetworkEvents {
    public static final Event<LoggedIn> LOGGED_IN = Event.create(LoggedIn.class);
    public static final Event<LoggedOut> LOGGED_OUT = Event.create(LoggedOut.class);

    private ServerPlayerNetworkEvents() {}

    public static final class LoggedIn extends EventBase {
        public final ServerGamePacketListenerImpl networkHandler;
        public final MinecraftServer server;
        public final ServerPlayer player;
        public final PacketSender packetSender;

        @ApiStatus.Internal
        public LoggedIn(ServerGamePacketListenerImpl networkHandler, MinecraftServer server, ServerPlayer player, PacketSender packetSender) {
            this.networkHandler = networkHandler;
            this.server = server;
            this.player = player;
            this.packetSender = packetSender;
        }
    }

    public static final class LoggedOut extends EventBase {
        public final ServerGamePacketListenerImpl handler;
        public final MinecraftServer server;

        @ApiStatus.Internal
        public LoggedOut(ServerGamePacketListenerImpl handler, MinecraftServer server) {
            this.handler = handler;
            this.server = server;
        }
    }
}
