package de.leowgc.moonlightcore.api.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.leowgc.moonlightcore.api.event.common.RegistryEvents;
import de.leowgc.moonlightcore.api.util.nullness.NotnullSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class DeferredRegister<R> {

    private final List<Entry<R, ?>> entries = new ArrayList<>();

    private final String modId;
    private final ResourceKey<? extends Registry<R>> registryKey;

    public DeferredRegister(String modId, ResourceKey<? extends Registry<R>> registryKey) {
        this.modId = modId;
        this.registryKey = registryKey;
    }

    public <T extends R> NotnullSupplier<T> register(String name, NotnullSupplier<T> value) {
        ResourceLocation id = new ResourceLocation(this.modId, name);
        this.entries.add(new Entry<>(id, value));

        return value;
    }

    public void bind() {
        RegistryEvents.REGISTER.subscribe((currentRegistryKey, output) -> {
            if(currentRegistryKey == this.registryKey) {
                this.entries.forEach(entry -> output.register(this.registryKey, entry.id(), entry.value()));
            }
        });
    }

    public List<R> value() {
        return ImmutableList.copyOf(this.entries.stream().map(Entry::value).map(NotnullSupplier::get).toList());
    }

    public Set<ResourceLocation> keys() {
        return ImmutableSet.copyOf(this.entries.stream().map(Entry::id).collect(Collectors.toSet()));
    }

    public record Entry<R, T extends R>(ResourceLocation id, NotnullSupplier<T> value) {}
}
