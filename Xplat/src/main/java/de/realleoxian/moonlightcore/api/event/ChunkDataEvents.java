package de.realleoxian.moonlightcore.api.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class ChunkDataEvents {
    public static final EventBus<ChunkDataEvents.Load> LOAD = EventBus.create(Load.class, (listeners) -> (level, chunkAccess, tag) -> {
       for(ChunkDataEvents.Load listener : listeners) {
           listener.onChunkDataLoad(level, chunkAccess, tag);
       }
    });
    public static final EventBus<ChunkDataEvents.Save> SAVE = EventBus.create(Save.class, (listeners) -> (level, chunkAccess, tag) -> {
       for(ChunkDataEvents.Save listener : listeners) {
           listener.onChunkDataSave(level, chunkAccess, tag);
       }
    });

    private ChunkDataEvents() {}

    public interface Load {
        void onChunkDataLoad(ServerLevel level, ChunkAccess chunkAccess, CompoundTag tag);
    }

    public interface Save {
        void onChunkDataSave(ServerLevel level, ChunkAccess chunkAccess, CompoundTag tag);
    }
}
