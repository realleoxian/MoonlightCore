package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class ServerChunkEvents {
    public static final Event<Load> LOAD = Event.create(Load.class, listeners -> (level, chunkAccess) -> {
       for (final var listener : listeners) {
           listener.onChunkLoad(level, chunkAccess);
       }
    });
    public static final Event<Unload> UNLOAD = Event.create(Unload.class, listeners -> (level, chunkAccess) -> {
        for (final var listener : listeners) {
            listener.onChunkUnload(level, chunkAccess);
        }
    });

    private ServerChunkEvents() {}

    @FunctionalInterface
    public interface Load {
        void onChunkLoad(ServerLevel level, ChunkAccess chunkAccess);
    }

    @FunctionalInterface
    public interface Unload {
        void onChunkUnload(ServerLevel level, ChunkAccess chunkAccess);
    }
}
