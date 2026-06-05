package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

public final class ServerTickEvents extends EventBase {
    public static final Event<ServerTickEvents> START = Event.create(ServerTickEvents.class);
    public static final Event<ServerTickEvents> END = Event.create(ServerTickEvents.class);

    public final MinecraftServer server;

    @ApiStatus.Internal
    public ServerTickEvents(MinecraftServer server) {
        this.server = server;
    }
}
