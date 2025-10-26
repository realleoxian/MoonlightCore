package de.leoxian.moonlightcore.event.common;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface RegisterEvent {
     /**
      * @see #onRegistration(ResourceKey, Output)
      */
     Event<RegisterEvent> EVENT = EventFactory.create(RegisterEvent.class);

     /**
      * Invoked to register new things on the game registry
      * @param currentRegistry The current registry is being processed
      * @param output The output of the registry
      */
     void onRegistration(ResourceKey<? extends Registry<?>> currentRegistry, Output output);

     interface Output {
          <T> void register(ResourceLocation id, Supplier<T> value);
     }
}
