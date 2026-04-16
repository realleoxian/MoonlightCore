package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.EventBus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class ClientChunkEvents {
    public static final EventBus<ClientChunkEvents.Load> LOAD = EventBus.create(Load.class, (listeners) -> (level, chunkAccess) -> {
        for(ClientChunkEvents.Load listener : listeners) {
            listener.onChunkLoad(level, chunkAccess);
        }
    });
    public static final EventBus<ClientChunkEvents.Unload> UNLOAD = EventBus.create(Unload.class, (listeners) -> (level, chunkAccess) -> {
        for(ClientChunkEvents.Unload listener : listeners) {
            listener.onChunkUnload(level, chunkAccess);
        }
    });

    private ClientChunkEvents() {}

    public interface Load {
        void onChunkLoad(ClientLevel level, ChunkAccess chunkAccess);
    }

    public interface Unload {
        void onChunkUnload(ClientLevel level, ChunkAccess chunkAccess);
    }

}
