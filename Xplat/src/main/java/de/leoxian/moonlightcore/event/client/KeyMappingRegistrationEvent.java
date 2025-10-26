package de.leoxian.moonlightcore.event.client;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.client.KeyMapping;

import java.util.function.Consumer;

public interface KeyMappingRegistrationEvent {
    /**
     * @see #onKeyMappingRegistration(Consumer)
     */
    Event<KeyMappingRegistrationEvent> EVENT = EventFactory.create(KeyMappingRegistrationEvent.class);

    /**
     * Invoked for registration of custom {@link KeyMapping}s
     * @param output The output of the event, used to register the given {@link KeyMapping}s
     */
    void onKeyMappingRegistration(Consumer<KeyMapping> output);

}
