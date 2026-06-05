package de.realleoxian.moonlightcore.api.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.ApiStatus;

public final class ChunkDataEvents extends EventBase implements CancellableEvent {
    public static final Event<ChunkDataEvents> LOAD = Event.create(ChunkDataEvents.class);
    public static final Event<ChunkDataEvents> SAVE = Event.create(ChunkDataEvents.class);

    public final ServerLevel level;
    public final ChunkAccess chunkAccess;
    public final CompoundTag tag;

    @ApiStatus.Internal
    public ChunkDataEvents(ServerLevel level, ChunkAccess chunkAccess, CompoundTag tag) {
        this.level = level;
        this.chunkAccess = chunkAccess;
        this.tag = tag;
    }
}
