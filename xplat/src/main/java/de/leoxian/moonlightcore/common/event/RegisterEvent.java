package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

@FunctionalInterface
public interface RegisterEvent {
	Event<RegisterEvent> EVENT = Event.create(RegisterEvent.class, listeners -> (registryKey, output) -> {
		for (RegisterEvent listener : listeners) {
			listener.onRegister(registryKey, output);
		}
	});

	void onRegister(ResourceKey<? extends Registry<?>> registryKey, Output output);

	@FunctionalInterface
	interface Output {
		<T> T register(Identifier id, T value);
	}
}
