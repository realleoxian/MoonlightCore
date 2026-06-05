package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

public final class ServerEntityEvents extends EventBase {
    public static final Event<ServerEntityEvents> LOAD = Event.create(ServerEntityEvents.class);
    public static final Event<ServerEntityEvents> UNLOAD = Event.create(ServerEntityEvents.class);

    public final ServerLevel level;
    public final Entity entity;

    @ApiStatus.Internal
    public ServerEntityEvents(ServerLevel level, Entity entity) {
        this.level = level;
        this.entity = entity;
    }
}
