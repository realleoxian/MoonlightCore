package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.ApiStatus;

public final class ServerLevelTickEvents extends EventBase {
    public static final Event<ServerLevelTickEvents> START = Event.create(ServerLevelTickEvents.class);
    public static final Event<ServerLevelTickEvents> END = Event.create(ServerLevelTickEvents.class);

    public final ServerLevel level;

    @ApiStatus.Internal
    public ServerLevelTickEvents(ServerLevel level) {
        this.level = level;
    }
}
