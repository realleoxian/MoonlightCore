package de.leoxian.moonlightcore.registry;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import de.leoxian.moonlightcore.event.common.RegisterEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DeferredRegistrar<R> {
    public static <R> DeferredRegistrar<R> create(Registry<R> registryType, String modId) {
        return create(registryType.key(), modId);
    }

    public static <R> DeferredRegistrar<R> create(ResourceKey<? extends Registry<R>> registryType, String modId) {
        return new DeferredRegistrar<>(registryType, modId);
    }

    private final Map<RegistryEntry<R, ?>, Supplier<? extends R>> entries = new LinkedHashMap<>();
    private final Set<RegistryEntry<R, ?>> entriesView = Collections.unmodifiableSet(entries.keySet());

    private boolean seenRegisterEvent = false;

    protected final ResourceKey<? extends Registry<R>> registryType;
    protected final String modId;
    protected final Logger logger;

    protected DeferredRegistrar(ResourceKey<? extends Registry<R>> registryType, String modId) {
        this.registryType = registryType;
        this.modId = modId;
        this.logger = LoggerFactory.getLogger("MoonlightCore | DeferredRegistrar/" + modId);
    }

    public void bind() {
        if(!entries.isEmpty()) {
            RegisterEvent.EVENT.subscribe((current, output) -> {
                if(current == registryType) {
                    logger.debug("Registering a total of {} known objects on registry {}", entries.size(), registryType);

                    entries.forEach((e, s) -> {
                        output.register(e.getName(), s);
                        e.updateReference(false);
                    });
                }
            });
        }

        seenRegisterEvent = true;
    }

    public <T extends R> RegistryEntry<R, T> register(final String name, final Supplier<T> sup) {
        Preconditions.checkArgument(!seenRegisterEvent, "Cannot register new entries to DeferredRegistrar after RegisterEvent was fired");
        Objects.requireNonNull(name, "RegistryEntry name cannot be null");
        Objects.requireNonNull(sup, "RegistryEntry value cannot be null");

        final ResourceLocation id = new ResourceLocation(modId, name);
        RegistryEntry<R, T> entry = RegistryEntry.create(registryType, id);
        if(this.entries.putIfAbsent(entry, sup) != null) {
            throw new IllegalArgumentException("Duplicated entry with name: " + id);
        }

        return entry;
    }

    public @UnmodifiableView Set<RegistryEntry<R, ?>> getEntries() {
        return entriesView;
    }
}
