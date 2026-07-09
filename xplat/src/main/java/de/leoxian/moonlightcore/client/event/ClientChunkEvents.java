package de.leoxian.moonlightcore.client.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class ClientChunkEvents {
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

    private ClientChunkEvents() {}

    @FunctionalInterface
    public interface Load {
        void onChunkLoad(ClientLevel level, ChunkAccess chunkAccess);
    }

    @FunctionalInterface
    public interface Unload {
        void onChunkUnload(ClientLevel level, ChunkAccess chunkAccess);
    }
}
