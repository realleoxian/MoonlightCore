package de.leoxian.moonlightcore.registry;

import de.leoxian.moonlightcore.event.common.RegisterEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RegistryHelper {
    public static RegistryHelper create(String modId) {
        return new RegistryHelper(modId);
    }

    private final Map<ResourceKey<? extends Registry<?>>, List<Registrar<?>>> registrars = new HashMap<>();
    private final String modId;

    private boolean initialized = false;

    private RegistryHelper(String modId) {
        this.modId = modId;
    }

    public <R> void addRegistrar(ResourceKey<? extends Registry<R>> registryType, Registrar<R> registrar) {
        if(this.initialized) {
            throw new IllegalStateException("Registry helper for mod '%s' is already initialized".formatted(this.modId));
        }

        this.registrars.computeIfAbsent(registryType, $ -> new ArrayList<>()).add(registrar);
    }

    public void bind() {
        if(this.initialized) {
            return;
        }

        RegisterEvent.EVENT.subscribe((currentRegistry, output) -> {
            if(this.registrars.containsKey(currentRegistry)) {
                for(var registrar : this.registrars.get(currentRegistry)) {
                    registrar.registerAll((entry) -> output.register(entry.id(), entry::get));
                }
            }
        });

        this.initialized = true;
    }

    public interface Registrar<R> {
        void registerAll(Consumer<RegistryEntry<R, ?>> output);
    }
}
