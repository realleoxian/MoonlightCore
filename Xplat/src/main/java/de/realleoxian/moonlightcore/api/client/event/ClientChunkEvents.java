package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.EventBus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class ClientChunkEvents {
    /**
     * @see Load#onChunkLoad(ClientLevel, ChunkAccess)
     */
    public static final EventBus<ClientChunkEvents.Load> LOAD = EventBus.create((listeners) -> (level, chunkAccess) -> {
        for(ClientChunkEvents.Load listener : listeners) {
            listener.onChunkLoad(level, chunkAccess);
        }
    });
    /**
     * @see Unload#onChunkUnload(ClientLevel, ChunkAccess)
     */
    public static final EventBus<ClientChunkEvents.Unload> UNLOAD = EventBus.create((listeners) -> (level, chunkAccess) -> {
        for(ClientChunkEvents.Unload listener : listeners) {
            listener.onChunkUnload(level, chunkAccess);
        }
    });

    private ClientChunkEvents() {}

    public interface Load {
        /**
         * Invoked on the client-side of a level when a chunk its loaded
         * @param level         The level instance the chunk is in
         * @param chunkAccess   The chunk instance that is being loaded
         */
        void onChunkLoad(ClientLevel level, ChunkAccess chunkAccess);
    }

    public interface Unload {
        /**
         * Invoked on the client-side of a level when a chunk its about to be unloaded
         * @param level         The level instance the chunk is in
         * @param chunkAccess   The chunk instance that is being unloaded
         */
        void onChunkUnload(ClientLevel level, ChunkAccess chunkAccess);
    }

}
