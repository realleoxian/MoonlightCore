package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

public final class ServerLifecycleEvents extends EventBase {
    public static final Event<ServerLifecycleEvents> STARTING = Event.create(ServerLifecycleEvents.class);
    public static final Event<ServerLifecycleEvents> STARTED = Event.create(ServerLifecycleEvents.class);
    public static final Event<ServerLifecycleEvents> STOPPING = Event.create(ServerLifecycleEvents.class);
    public static final Event<ServerLifecycleEvents> STOPPED = Event.create(ServerLifecycleEvents.class);

    public final MinecraftServer server;

    @ApiStatus.Internal
    public ServerLifecycleEvents(MinecraftServer server) {
        this.server = server;
    }
}
