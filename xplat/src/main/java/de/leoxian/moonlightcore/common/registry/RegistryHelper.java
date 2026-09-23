package de.leoxian.moonlightcore.common.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.common.event.RegisterEvent;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.function.BiConsumer;

public class RegistryHelper implements RegisterEvent {
    public interface Registrar<R> {
        void registerAll(BiConsumer<String, R> output);
    }

    public static RegistryHelper create(final String modId) {
        return new RegistryHelper(modId);
    }

    private final Multimap<ResourceKey<? extends Registry<?>>, Registrar<?>> registrars = ArrayListMultimap.create();
    private final String modId;

    private RegistryHelper(String modId) {
        this.modId = modId;
        RegisterEvent.EVENT.subscribe(this);
    }

    @Override
    public void onRegister(ResourceKey<? extends Registry<?>> registryKey, Output output) {
        this.registrars.forEach((resourceKey, registrar) -> {
            if (resourceKey != registryKey) {
                return;
            }

            registrar.registerAll((name, value) -> output.register(Identifier.fromNamespaceAndPath(this.modId, name), value));
        });
    }

    public <R> void add(ResourceKey<? extends Registry<R>> registryKey, Registrar<R> registrar) {
        this.registrars.put(registryKey, registrar);
    }
}
