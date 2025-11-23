package de.leoxian.moonlightcore.event.common;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

public interface RegistryCreationEvent {
    /**
     * @see #onRegistryCreation(Output)
     */
    Event<RegistryCreationEvent> EVENT = EventFactory.create(RegistryCreationEvent.class);

    /**
     * Invoked for the registration of custom registries
     * @param output The output of the event, used to register the registries
     */
    void onRegistryCreation(Output output);

    interface Output {
        /**
         * Registers the given {@link Registry} and if it will be synced
         * @param registry The registry that is going to be registered
         * @param sync If the registry will be synced
         * @param <T> The type value of the registry
         */
        <T> void register(Registry<T> registry, boolean sync);

        /**
         * Registers the given {@link Registry} but being unsynced. Equivalent to calling {@link #register(Registry, boolean)} with the sync being false
         * @param registry The registry that is going to be registered
         * @param <T> The type value of the registry
         */
        default <T> void register(Registry<T> registry) {
            this.register(registry, false);
        }
    }
}
