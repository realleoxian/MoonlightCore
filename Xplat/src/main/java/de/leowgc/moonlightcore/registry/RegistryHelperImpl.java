package de.leowgc.moonlightcore.registry;

import com.google.common.collect.ImmutableSet;
import de.leowgc.moonlightcore.api.event.common.RegistryEvents;
import de.leowgc.moonlightcore.api.registry.RegistryEntry;
import de.leowgc.moonlightcore.api.registry.RegistryHelper;
import de.leowgc.moonlightcore.api.util.nullness.NotnullSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public final class RegistryHelperImpl<R> implements RegistryHelper<R> {
    public static <R> RegistryHelper<R> create(String modId, ResourceKey<? extends Registry<R>> registryType) {
        return new RegistryHelperImpl<>(registryType, modId);
    }

    private final Set<RegistryEntry<R, ?>> entries = new HashSet<>();
    private final ResourceKey<? extends Registry<R>> registryType;
    private final String modId;

    private RegistryHelperImpl(ResourceKey<? extends Registry<R>> registryType, String modId) {
        this.registryType = registryType;
        this.modId = modId;
    }

    @Override
    public <T extends R> RegistryEntry<R, T> register(String id, NotnullSupplier<T> valueSupplier) {
        ResourceLocation entryId = new ResourceLocation(this.modId, id);
        RegistryEntry<R, T> entry = new RegistryEntryImpl<>(this.registryType, entryId, valueSupplier);
        this.entries.add(entry);

        return entry;
    }

    @Override
    public void bind() {
        RegistryEvents.REGISTER.subscribe((currentRegistry, output) -> {
            if(currentRegistry.equals(this.registryType)) {
                this.entries.forEach((entry) -> output.register(this.registryType, entry.id(), entry));
            }
        });
    }

    @Override
    public ImmutableSet<RegistryEntry<R, ?>> entries() {
        return ImmutableSet.copyOf(this.entries);
    }

    @Override
    public ResourceKey<? extends Registry<R>> registryType() {
        return this.registryType;
    }
}
