package de.realleoxian.moonlightcore.forge.network;

import de.realleoxian.moonlightcore.api.event.ServerPlayerNetworkEvents;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ServerPacketHandler {
    private static @Nullable ServerGamePacketListenerImpl handler;
    private static @Nullable MinecraftServer currentServer;

    public static void setContext(ServerGamePacketListenerImpl handler, MinecraftServer server) {
        ServerPacketHandler.handler = handler;
        ServerPacketHandler.currentServer = server;
    }

    public static void invalidate() {
        ServerPacketHandler.handler = null;
        ServerPacketHandler.currentServer = null;
    }

    public static void dispatchLoggedInEvent(ServerPlayer player) {
        checkValid();
        ServerPlayerNetworkEvents.LOGGED_IN.invoker().onPlayerLoggedIn(ServerPacketHandler.handler, PacketSender.ofPlayer(player), ServerPacketHandler.currentServer);
    }

    public static void dispatchLOggedOutEvent() {
        checkValid();
        ServerPlayerNetworkEvents.LOGGED_OUT.invoker().onPlayerLoggedOut(ServerPacketHandler.handler, ServerPacketHandler.currentServer);
    }

    private static void checkValid() {
        if (ServerPacketHandler.handler == null || ServerPacketHandler.currentServer == null) {
            throw new IllegalStateException("There is not a valid server to perform any action from this class");
        }
    }
}
