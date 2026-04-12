package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class ServerBlockEntityEvents {
    /**
     * @see Load#onBlockEntityLoad(ServerLevel, BlockEntity)
     */
    public static final EventBus<ServerBlockEntityEvents.Load> LOAD = EventBus.create((listeners) -> (level, blockEntity) -> {
        for(Load listener : listeners) {
            listener.onBlockEntityLoad(level, blockEntity);
        }
    });
    /**
     * @see Unload#onBlockEntityUnload(ServerLevel, BlockEntity)
     */
    public static final EventBus<ServerBlockEntityEvents.Unload> UNLOAD = EventBus.create((listeners) -> (level, blockEntity) -> {
       for(Unload listener : listeners) {
           listener.onBlockEntityUnload(level, blockEntity);
       }
    });

    private ServerBlockEntityEvents() {}

    public interface Load {
        /**
         * Invoked when a {@link BlockEntity block entity} its loaded into a {@link ServerLevel}
         * @param level the level the block entity is loaded in
         * @param blockEntity the block entity
         */
        void onBlockEntityLoad(ServerLevel level, BlockEntity blockEntity);
    }

    public interface Unload {
        /**
         * Invoked when a {@link BlockEntity block entity} its about to be unloaded from a {@link ServerLevel}
         * @param level the level the block entity is about to be unloaded from
         * @param blockEntity the block entity
         */
        void onBlockEntityUnload(ServerLevel level, BlockEntity blockEntity);
    }
}
