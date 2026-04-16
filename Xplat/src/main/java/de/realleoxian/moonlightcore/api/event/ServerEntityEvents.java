package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public final class ServerEntityEvents {
    public static final EventBus<Load> LOAD = EventBus.create(Load.class, (listeners) -> (level, entity) -> {
       for (Load listener : listeners) {
           listener.onLoad(level, entity);
       }
    });
    public static final EventBus<Unload> UNLOAD = EventBus.create(Unload.class, (listeners) -> (level, entity) -> {
        for (Unload listener : listeners) {
            listener.onUnload(level, entity);
        }
    });

    private ServerEntityEvents() {}

    public interface Load {
        void onLoad(ServerLevel level, Entity entity);
    }

    public interface Unload {
        void onUnload(ServerLevel level, Entity entity);
    }
}
