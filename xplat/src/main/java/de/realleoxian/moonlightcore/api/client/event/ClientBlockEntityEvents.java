package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientBlockEntityEvents extends EventBase {
    public static final Event<ClientBlockEntityEvents> LOAD = Event.create(ClientBlockEntityEvents.class);
    public static final Event<ClientBlockEntityEvents> UNLOAD = Event.create(ClientBlockEntityEvents.class);

    public final ClientLevel level;
    public final BlockEntity blockEntity;

    public ClientBlockEntityEvents(ClientLevel level, BlockEntity blockEntity) {
        this.level = level;
        this.blockEntity = blockEntity;
    }
}
