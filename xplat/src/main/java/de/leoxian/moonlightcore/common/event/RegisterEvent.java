package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import jdk.jfr.Registered;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@FunctionalInterface
public interface RegisterEvent {
    Event<RegisterEvent> EVENT = Event.create(RegisterEvent.class, listeners -> (registryKey, output) -> {
       for (final var listener : listeners) {
           listener.onRegister(registryKey, output);
       }
    });

    void onRegister(ResourceKey<? extends Registry<?>> registryKey, Output output);

    @ApiStatus.NonExtendable
    interface Output {
        <T> void register(Identifier id, Supplier<T> value);
    }
}
