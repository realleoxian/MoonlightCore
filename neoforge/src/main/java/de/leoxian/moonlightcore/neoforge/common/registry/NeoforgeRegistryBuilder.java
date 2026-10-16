package de.leoxian.moonlightcore.neoforge.common.registry;

import de.leoxian.moonlightcore.common.registry.RegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.*;

public class NeoforgeRegistryBuilder<R> implements RegistryBuilder<R> {
	private final ResourceKey<Registry<R>> registryKey;
	private boolean sync = false;
	private Identifier defaultId = null;

	public NeoforgeRegistryBuilder(ResourceKey<Registry<R>> registryKey) {
		this.registryKey = registryKey;
	}

	@Override
	public RegistryBuilder<R> sync(boolean sync) {
		this.sync = sync;
		return this;
	}
	@Override
	public RegistryBuilder<R> defaultId(Identifier id) {
		this.defaultId = id;
		return this;
	}

	@Override
	public Registry<R> build() {
		net.neoforged.neoforge.registries.RegistryBuilder<R> neoBuilder = new net.neoforged.neoforge.registries.RegistryBuilder<>(this.registryKey);
		neoBuilder.sync(this.sync);
		neoBuilder.defaultKey(this.defaultId);
		return neoBuilder.create();
	}
}
