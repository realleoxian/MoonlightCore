package de.leoxian.moonlightcore.event.common;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface ChunkEvent {
     /**
      * @see Save#onChunkDataSave(ChunkAccess, ServerLevel, CompoundTag)
      */
     Event<Save> SAVE = EventFactory.create(Save.class);
     /**
      * @see Load#onChunkDataLoad(ChunkAccess, ServerLevel, CompoundTag)
      */
     Event<Load> LOAD = EventFactory.create(Load.class);

     interface Save {
          /**
           * Invoked when a chunk's data is saved, just before the data is written.
           * Add your own data to the {@link CompoundTag} parameter to get your data saved as well
           * @param chunkAccess The chunk that is saved
           * @param level The level the chunk is in
           * @param nbt The chunk data that is written to the save file
           */
          void onChunkDataSave(ChunkAccess chunkAccess, ServerLevel level, CompoundTag nbt);
     }
     
     interface Load {
          /**
           * Invoked just before aa chunk's data is fully read.
           * You can read out your own data from the {@link CompoundTag} parameter, when you have saved one before
           * @param chunkAccess The chunk that is loaded
           * @param level The leve the chunk is in, may be {@code null}
           * @param nbt The chunk data that was read from the save file
           */
          void onChunkDataLoad(ChunkAccess chunkAccess, @Nullable ServerLevel level, CompoundTag nbt);
     }
}
