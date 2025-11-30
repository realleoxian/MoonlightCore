package de.leoxian.moonlightcore.registry;

import de.leoxian.moonlightcore.event.common.RegisterEvent;
import de.leoxian.moonlightcore.util.nullness.NonnullSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class DeferredRegistrar<R> {

    public static <R> DeferredRegistrar<R> create(Registry<R> registry, String modId) {
        return create(registry.key(), modId);
    }

    public static <R> DeferredRegistrar<R> create(ResourceKey<? extends Registry<R>> registryType, String modId) {
        return new DeferredRegistrar<>(registryType, modId);
    }

    private final Map<RegistryEntry<R, ?>, NonnullSupplier<? extends R>> entries = new LinkedHashMap<>();
    private final Set<RegistryEntry<R, ?>> entriesView = Collections.unmodifiableSet(entries.keySet());

    private final ResourceKey<? extends Registry<R>> registryType;
    private final String modId;

    private boolean seenRegisterEvent = false;

    private DeferredRegistrar(ResourceKey<? extends Registry<R>> registryType, String modId) {
        this.registryType = registryType;
        this.modId = modId;
    }

    public void bind() {
        RegisterEvent.EVENT.subscribe((current, output) -> {
            if(current == registryType) {
                entries.forEach((e, s) -> {
                    output.register(e.getName(), s);
                    e.updateReference(false);
                });
            }
        });

        seenRegisterEvent = true;
    }

    public <T extends R> RegistryEntry<R, T> register(final String name, final NonnullSupplier<? extends T> sup) {
        if(seenRegisterEvent) {
            throw new IllegalStateException("Cannot register new entries to DeferredRegistrar after RegisterEvent has been fired");
        }
        Objects.requireNonNull(name, "RegistryEntry name cannot be null");
        Objects.requireNonNull(sup, "RegistryEntry value cannot be null");

        final ResourceLocation key = new ResourceLocation(modId, name);
        RegistryEntry<R, T> entry = RegistryEntry.create(registryType, key);
        if(entries.putIfAbsent(entry, sup) != null) {
            throw new IllegalArgumentException("Duplicated registration: " + name);
        }

        return entry;
    }

    public @UnmodifiableView Collection<RegistryEntry<R, ?>> getEntries() {
        return entriesView;
    }

    public ResourceKey<? extends Registry<R>> getRegistryType() {
        return registryType;
    }

}
