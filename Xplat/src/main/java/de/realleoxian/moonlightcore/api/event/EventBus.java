package de.realleoxian.moonlightcore.api.event;

import de.realleoxian.moonlightcore.impl.event.EventBusImpl;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface EventBus<T> {
    static <T> EventBus<T> create(Class<T> eventClass, Function<T[], T> factory) {
        return EventBusImpl.create(eventClass, factory);
    }

    T invoker();

    EventBus<T> definePhaseOrdering(ResourceLocation previous, ResourceLocation phase);

    void subscribe(EventPriority priority, ResourceLocation phase, T listener);

    void subscribe(EventPriority priority, T listener);

    void subscribe(ResourceLocation phase, T listener);

    void subscribe(T listener);
}
