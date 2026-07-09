package de.leoxian.moonlightcore.client.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.client.Minecraft;

public final class ClientLifecycleEvents {
    public static final Event<Started> STARTED = Event.create(Started.class, listeners -> minecraft -> {
        for (final var listener : listeners) {
            listener.onClientStarted(minecraft);
        }
    });
    public static final Event<Stopping> STOPPING = Event.create(Stopping.class, listeners -> minecraft -> {
        for (final var listener : listeners) {
            listener.onClientStopping(minecraft);
        }
    });

    private ClientLifecycleEvents() {}

    @FunctionalInterface
    public interface Started {
        void onClientStarted(Minecraft minecraft);
    }

    @FunctionalInterface
    public interface Stopping {
        void onClientStopping(Minecraft minecraft);
    }
}
