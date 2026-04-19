package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class ServerChunkEvents {
    public static final EventBus<ServerChunkEvents.Load> LOAD = EventBus.create(Load.class, (listeners) -> (level, chunkAccess) -> {
        for(ServerChunkEvents.Load listener : listeners) {
            listener.onChunkLoad(level, chunkAccess);
        }
    });
    public static final EventBus<ServerChunkEvents.Unload> UNLOAD = EventBus.create(Unload.class, (listeners) -> (level, chunkAccess) -> {
       for(ServerChunkEvents.Unload listener : listeners) {
           listener.onChunkUnload(level, chunkAccess);
       }
    });

    private ServerChunkEvents() {}

    public interface Load {
        void onChunkLoad(ServerLevel level, ChunkAccess chunkAccess);
    }

    public interface Unload {
        void onChunkUnload(ServerLevel level, ChunkAccess chunkAccess);
    }
}
