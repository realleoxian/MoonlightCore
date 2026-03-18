package de.leoxian.moonlightcore.api.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class ChunkDataEvents {
    /**
     * @see Load#onChunkDataLoad(ServerLevel, ChunkAccess, CompoundTag)
     */
    public static final EventBus<ChunkDataEvents.Load> LOAD = EventBus.create((listeners) -> (level, chunkAccess, tag) -> {
       for(ChunkDataEvents.Load listener : listeners) {
           listener.onChunkDataLoad(level, chunkAccess, tag);
       }
    });
    /**
     * @see Save#onChunkDataSave(ServerLevel, ChunkAccess, CompoundTag)
     */
    public static final EventBus<ChunkDataEvents.Save> SAVE = EventBus.create((listeners) -> (level, chunkAccess, tag) -> {
       for(ChunkDataEvents.Save listener : listeners) {
           listener.onChunkDataSave(level, chunkAccess, tag);
       }
    });

    private ChunkDataEvents() {}

    public interface Load {

        /**
         * Invoked when a chunk's data is being loaded, this can be used to load
         * custom data from chunks. This event its invoked on the server-side of
         * a level.
         * @param level         The level the chunk its in
         * @param chunkAccess   The chunk instance
         * @param tag           The NBT data of the chunk
         */
        void onChunkDataLoad(ServerLevel level, ChunkAccess chunkAccess, CompoundTag tag);
    }

    public interface Save {
        /**
         * Invoked when a chunk's data is being saved, this can be used to save custom data on chunks.
         * This event its invoked on the server-side of a level.
         * @param level         The level the chunk its in
         * @param chunkAccess   The chunk instance
         * @param tag           The NBT data of the chunk
         */
        void onChunkDataSave(ServerLevel level, ChunkAccess chunkAccess, CompoundTag tag);
    }
}
