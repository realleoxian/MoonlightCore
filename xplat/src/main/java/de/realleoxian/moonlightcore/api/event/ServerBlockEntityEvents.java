package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;

public final class ServerBlockEntityEvents extends EventBase {
    public static final Event<ServerBlockEntityEvents> LOAD = Event.create(ServerBlockEntityEvents.class);
    public static final Event<ServerBlockEntityEvents> UNLOAD = Event.create(ServerBlockEntityEvents.class);

    public final ServerLevel level;
    public final BlockEntity blockEntity;

    @ApiStatus.Internal
    public ServerBlockEntityEvents(ServerLevel level, BlockEntity blockEntity) {
        this.level = level;
        this.blockEntity = blockEntity;
    }
}
