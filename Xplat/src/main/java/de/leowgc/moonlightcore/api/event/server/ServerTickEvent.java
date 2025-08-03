package de.leowgc.moonlightcore.api.event.server;

import de.leowgc.moonlightcore.api.event.Event;
import net.minecraft.server.MinecraftServer;

public interface ServerTickEvent {
    Event<ServerTickEvent> SERVER_TICK = Event.create();

    void bootstrap(MinecraftServer server, Phase phase);

    enum Phase {
        START,
        END;
    }
}
