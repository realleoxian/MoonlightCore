package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.core.Registry;

import java.util.function.Consumer;

@FunctionalInterface
public interface NewRegistryEvent {
    Event<NewRegistryEvent> EVENT = Event.create(NewRegistryEvent.class, listeners -> output -> {
       for (final var listener : listeners) {
           listener.onNewRegistryEvent(output);
       }
    });

    void onNewRegistryEvent(Consumer<Registry<?>> output);
}
