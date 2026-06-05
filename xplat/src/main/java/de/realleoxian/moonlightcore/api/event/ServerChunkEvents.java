package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class ServerChunkEvents extends EventBase {
    public static final Event<ServerChunkEvents> LOAD = Event.create(ServerChunkEvents.class);
    public static final Event<ServerChunkEvents> UNLOAD = Event.create(ServerChunkEvents.class);

    public final ServerLevel level;
    public final ChunkAccess chunkAccess;

    public ServerChunkEvents(ServerLevel level, ChunkAccess chunkAccess) {
        this.level = level;
        this.chunkAccess = chunkAccess;
    }
}
