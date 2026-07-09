package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class ServerBlockEntityEvents {
    public static final Event<Load> LOAD = Event.create(Load.class, listeners -> (level, blockEntity) -> {
       for (final var listener : listeners) {
           listener.onBlockEntityLoad(level, blockEntity);
       }
    });
    public static final Event<Unload> UNLOAD = Event.create(Unload.class, listeners -> (level, blockEntity) -> {
        for (final var listener : listeners) {
            listener.onBlockEntityUnload(level, blockEntity);
        }
    });

    private ServerBlockEntityEvents() {}

    @FunctionalInterface
    public interface Load {
        void onBlockEntityLoad(ServerLevel level, BlockEntity blockEntity);
    }

    @FunctionalInterface
    public interface Unload {
        void onBlockEntityUnload(ServerLevel level, BlockEntity blockEntity);
    }
}
