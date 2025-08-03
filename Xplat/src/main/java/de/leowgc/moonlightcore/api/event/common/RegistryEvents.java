package de.leowgc.moonlightcore.api.event.common;

import de.leowgc.moonlightcore.api.event.Event;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface RegistryEvents {
    Event<NewRegistry> NEW_REGISTRY = Event.create();
    Event<Register> REGISTER = Event.create();

    @FunctionalInterface
    interface NewRegistry {
        void bootstrap(Output output);

        interface Output {
            void register(WritableRegistry<?> registry, boolean syncedRegistry);
        }
    }

    @FunctionalInterface
    interface Register {
        void bootstrap(ResourceKey<? extends Registry<?>> currentRegistryKey, Output output);

        interface Output {
            <R, T extends R> void register(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation id, Supplier<T> value);
        }
    }
}
