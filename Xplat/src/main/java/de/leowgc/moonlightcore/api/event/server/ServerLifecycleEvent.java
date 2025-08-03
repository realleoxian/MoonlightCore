package de.leowgc.moonlightcore.api.event.server;

import de.leowgc.moonlightcore.api.event.Event;
import de.leowgc.moonlightcore.api.util.SidedEnvironment;
import net.minecraft.server.MinecraftServer;

@FunctionalInterface
@SidedEnvironment(SidedEnvironment.Environment.SERVER)
public interface ServerLifecycleEvent {
    /**
     * An event fired when an {@link MinecraftServer} is starting
     */
    Event<ServerLifecycleEvent> STARTING = Event.create();
    /**
     * An event fired when an {@link MinecraftServer} has been started
     */
    Event<ServerLifecycleEvent> STARTED = Event.create();
    /**
     * An event fired when an {@link MinecraftServer} is stopping
     */
    Event<ServerLifecycleEvent> STOPPING = Event.create();
    /**
     * An event fired when an {@link MinecraftServer} has been stopped
     */
    Event<ServerLifecycleEvent> STOPPED = Event.create();

    void bootstrap(MinecraftServer server);
}
