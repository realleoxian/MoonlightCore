package de.leoxian.moonlightcore.util;

import de.leoxian.moonlightcore.api.event.common.RegistryEvents;
import de.leoxian.moonlightcore.api.util.nullness.NotnullSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public abstract class ModRegister<R> {
    private final Map<ResourceLocation, NotnullSupplier<? extends R>> entries = new HashMap<>();

    private final String modId;
    private final ResourceKey<? extends Registry<R>> registryKey;

    ModRegister(String modId, ResourceKey<? extends Registry<R>> registryKey) {
        this.modId = modId;
        this.registryKey = registryKey;
    }

    public <T extends R> NotnullSupplier<T> register(String name, NotnullSupplier<T> valueSupplier) {
        ResourceLocation id = new ResourceLocation(this.modId, name);

        if(this.entries.containsKey(id)) throw new IllegalStateException("Duplicated entry key: " + id);

        this.entries.put(id, valueSupplier);
        return valueSupplier;
    }

    public void bind() {
        if(!this.entries.isEmpty()) {
            RegistryEvents.REGISTER.subscribe((currentKey, output) -> {
                if(currentKey == this.registryKey) {
                    this.entries.forEach((id, valueSupplier) -> output.register(this.registryKey, id, valueSupplier));
                }
            });
        }
    }
}
