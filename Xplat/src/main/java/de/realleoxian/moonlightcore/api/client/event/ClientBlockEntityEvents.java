package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.EventBus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class ClientBlockEntityEvents {
    public static final EventBus<ClientBlockEntityEvents.Load> LOAD = EventBus.create(ClientBlockEntityEvents.Load.class, (listeners) -> (level, blockEntity) -> {
        for(ClientBlockEntityEvents.Load listener : listeners) {
            listener.onBlockEntityLoad(level, blockEntity);
        }
    });
    public static final EventBus<ClientBlockEntityEvents.Unload> UNLOAD = EventBus.create(ClientBlockEntityEvents.Unload.class, (listeners) -> (level, blockEntity) -> {
        for(ClientBlockEntityEvents.Unload listener : listeners) {
            listener.onBlockEntityUnload(level, blockEntity);
        }
    });

    private ClientBlockEntityEvents() {}

    public interface Load {
        void onBlockEntityLoad(ClientLevel level, BlockEntity blockEntity);
    }

    public interface Unload {
        void onBlockEntityUnload(ClientLevel level, BlockEntity blockEntity);
    }
}
