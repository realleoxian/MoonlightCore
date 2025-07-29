package de.leoxian.moonlightcore.api.event.client;

import de.leoxian.moonlightcore.api.event.Event;
import de.leoxian.moonlightcore.api.util.SidedEnvironment;
import net.minecraft.client.Minecraft;

@FunctionalInterface
@SidedEnvironment(SidedEnvironment.Environment.CLIENT)
public interface ClientLifecycleEvent {
    /**
     * An event fired when minecraft's client is starting
     */
    Event<ClientLifecycleEvent> STARTING = Event.create();
    /**
     * An event fired when minecraft's client has been started
     */
    Event<ClientLifecycleEvent> STARTED = Event.create();
    /**
     * An event fired when minecraft's client is stopping
     */
    Event<ClientLifecycleEvent> STOPPING = Event.create();

    void bootstrap(Minecraft minecraft);
}
