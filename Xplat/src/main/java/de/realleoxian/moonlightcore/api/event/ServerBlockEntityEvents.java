package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class ServerBlockEntityEvents {
    public static final EventBus<ServerBlockEntityEvents.Load> LOAD = EventBus.create(Load.class, (listeners) -> (level, blockEntity) -> {
        for(Load listener : listeners) {
            listener.onBlockEntityLoad(level, blockEntity);
        }
    });
    public static final EventBus<ServerBlockEntityEvents.Unload> UNLOAD = EventBus.create(Unload.class, (listeners) -> (level, blockEntity) -> {
       for(Unload listener : listeners) {
           listener.onBlockEntityUnload(level, blockEntity);
       }
    });

    private ServerBlockEntityEvents() {}

    public interface Load {
        void onBlockEntityLoad(ServerLevel level, BlockEntity blockEntity);
    }

    public interface Unload {
        void onBlockEntityUnload(ServerLevel level, BlockEntity blockEntity);
    }
}
