package de.leoxian.moonlightcore.client.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.client.Minecraft;

public final class ClientTickEvents {
    public static final Event<Start> START = Event.create(Start.class, listeners -> minecraft -> {
        for (final var listener : listeners) {
            listener.onClientTickStart(minecraft);
        }
    });
    public static final Event<End> END = Event.create(End.class, listeners -> minecraft -> {
        for (final var listener : listeners) {
            listener.onClientTickEnd(minecraft);
        }
    });

    private ClientTickEvents() {}

    @FunctionalInterface
    public interface Start {
        void onClientTickStart(Minecraft minecraft);
    }

    @FunctionalInterface
    public interface End {
        void onClientTickEnd(Minecraft minecraft);
    }
}
