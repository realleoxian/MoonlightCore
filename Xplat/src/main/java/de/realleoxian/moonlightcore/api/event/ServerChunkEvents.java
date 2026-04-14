package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class ServerChunkEvents {
    /**
     * @see Load#onChunkLoad(ServerLevel, ChunkAccess)
     */
    public static final EventBus<ServerChunkEvents.Load> LOAD = EventBus.create(Load.class, (listeners) -> (level, chunkAccess) -> {
        for(ServerChunkEvents.Load listener : listeners) {
            listener.onChunkLoad(level, chunkAccess);
        }
    });
    /**
     * @see Unload#onChunkUnload(ServerLevel, ChunkAccess)
     */
    public static final EventBus<ServerChunkEvents.Unload> UNLOAD = EventBus.create(Unload.class, (listeners) -> (level, chunkAccess) -> {
       for(ServerChunkEvents.Unload listener : listeners) {
           listener.onChunkUnload(level, chunkAccess);
       }
    });


    private ServerChunkEvents() {}

    public interface Load {
        /**
         * Invoked on the server-side of a level when a chunk is being loaded
         * @param level       The level the chunk its in
         * @param chunkAccess The chunk instance
         */
        void onChunkLoad(ServerLevel level, ChunkAccess chunkAccess);
    }

    public interface Unload {
        /**
         * Invoked on the server-side of a level when a chunk its just going to be unloaded
         * @param level         The level the chunk its in
         * @param chunkAccess   The chunk instance
         */
        void onChunkUnload(ServerLevel level, ChunkAccess chunkAccess);
    }
}
