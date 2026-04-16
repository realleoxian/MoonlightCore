package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.EventBus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public final class ClientEntityEvents {
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

    private ClientEntityEvents() {}

    public interface Load {
        void onLoad(ClientLevel level, Entity entity);
    }

    public interface Unload {
        void onUnload(ClientLevel level, Entity entity);
    }
}
