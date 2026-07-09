package de.leoxian.moonlightcore.client.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class ClientBlockEntityEvents {
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

    private ClientBlockEntityEvents() {}

    @FunctionalInterface
    public interface Load {
        void onBlockEntityLoad(ClientLevel level, BlockEntity blockEntity);
    }

    @FunctionalInterface
    public interface Unload {
        void onBlockEntityUnload(ClientLevel level, BlockEntity blockEntity);
    }
}
