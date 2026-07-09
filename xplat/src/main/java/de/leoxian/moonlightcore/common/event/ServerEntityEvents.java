package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class ServerEntityEvents {
    public static final Event<Load> LOAD = Event.create(Load.class, listeners -> (level, entity) -> {
       for (final var listener : listeners) {
           listener.onEntityLoad(level, entity);
       }
    });
    public static final Event<Unload> UNLOAD = Event.create(Unload.class, listeners -> (level, entity) -> {
        for (final var listener : listeners) {
            listener.onEntityUnload(level, entity);
        }
    });

    private ServerEntityEvents() {}

    @FunctionalInterface
    public interface Load {
        void onEntityLoad(ServerLevel level, Entity entity);
    }

    @FunctionalInterface
    public interface Unload {
        void onEntityUnload(ServerLevel level, Entity entity);
    }
}
