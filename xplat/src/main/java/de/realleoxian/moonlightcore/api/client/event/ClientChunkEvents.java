package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.ApiStatus;

public final class ClientChunkEvents extends EventBase {
    public static final Event<ClientChunkEvents> LOAD = Event.create(ClientChunkEvents.class);
    public static final Event<ClientChunkEvents> UNLOAD = Event.create(ClientChunkEvents.class);

    public final ClientLevel level;
    public final ChunkAccess chunkAccess;

    @ApiStatus.Internal
    public ClientChunkEvents(ClientLevel level, ChunkAccess chunkAccess) {
        this.level = level;
        this.chunkAccess = chunkAccess;
    }
}
