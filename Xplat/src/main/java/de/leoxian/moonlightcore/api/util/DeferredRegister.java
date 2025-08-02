package de.leoxian.moonlightcore.api.util;

import de.leoxian.moonlightcore.api.event.common.RegistryEvents;
import de.leoxian.moonlightcore.api.util.nullness.NotnullSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

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

    private record Entry<R, T extends R>(ResourceLocation id, NotnullSupplier<T> value) {}

}
