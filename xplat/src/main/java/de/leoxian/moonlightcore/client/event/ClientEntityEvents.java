package de.leoxian.moonlightcore.client.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public final class ClientEntityEvents {
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

    private ClientEntityEvents() {}

    @FunctionalInterface
    public interface Load {
        void onEntityLoad(ClientLevel level, Entity entity);
    }

    @FunctionalInterface
    public interface Unload {
        void onEntityUnload(ClientLevel level, Entity entity);
    }
}
