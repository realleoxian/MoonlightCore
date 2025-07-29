package de.leoxian.moonlightcore.api.event.server;

import de.leoxian.moonlightcore.api.event.Event;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

public interface ServerTickEvent {
    Event<ServerTickEvent> SERVER_TICK = Event.create();

    void bootstrap(MinecraftServer server, Phase phase);

    enum Phase {
        START,
        END;
    }
}
