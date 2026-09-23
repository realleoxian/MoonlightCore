package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.core.Registry;

import java.util.function.Consumer;

@FunctionalInterface
public interface NewRegistryEvent {
	Event<NewRegistryEvent> EVENT = Event.create(NewRegistryEvent.class, listeners -> output -> {
		for (NewRegistryEvent listener : listeners) {
			listener.onNewRegistries(output);
		}
	});

	void onNewRegistries(Consumer<Registry<?>> output);
}
