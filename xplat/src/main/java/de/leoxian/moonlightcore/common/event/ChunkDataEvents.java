package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class ChunkDataEvents {
    public static final Event<Load> LOAD = Event.create(Load.class, listeners -> (level, chunkAccess, tag) -> {
       for (final var listener : listeners) {
           listener.onChunkDataLoad(level, chunkAccess, tag);
       }
    });
    public static final Event<Save> SAVE = Event.create(Save.class, listeners -> (level, chunkAccess, tag) -> {
        for (final var listener : listeners) {
            listener.onChunkDataSave(level, chunkAccess, tag);
        }
    });

    private ChunkDataEvents() {}

    @FunctionalInterface
    public interface Load {
        void onChunkDataLoad(ServerLevel level, ChunkAccess chunkAccess, CompoundTag tag);
    }

    @FunctionalInterface
    public interface Save {
        void onChunkDataSave(ServerLevel level, ChunkAccess chunkAccess, CompoundTag tag);
    }
}
