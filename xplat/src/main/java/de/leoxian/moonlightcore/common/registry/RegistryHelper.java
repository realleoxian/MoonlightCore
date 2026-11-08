package de.leoxian.moonlightcore.common.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.leoxian.moonlightcore.common.event.RegisterEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.BiConsumer;

public class RegistryHelper implements RegisterEvent {
	public interface Registrar<R> {
		void registerAll(BiConsumer<Identifier, R> output);
	}

	public static RegistryHelper create() {
		return new RegistryHelper();
	}

	private final Multimap<ResourceKey<? extends Registry<?>>, Registrar<?>> registrars = ArrayListMultimap.create();

	private RegistryHelper() {
		RegisterEvent.EVENT.subscribe(this);
	}

	@Override
	public void onRegister(ResourceKey<? extends Registry<?>> registryKey, Output output) {
		this.registrars.forEach((resourceKey, registrar) -> {
			if (resourceKey != registryKey) {
				return;
			}

			registrar.registerAll(output::register);
		});
	}

	public <R> void add(ResourceKey<? extends Registry<R>> registryKey, Registrar<R> registrar) {
		this.registrars.put(registryKey, registrar);
	}
}
