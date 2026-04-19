package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;

public final class ServerLevelTickEvents {
    public static final EventBus<Start> START = EventBus.create(Start.class, (listeners) -> (level) -> {
        for (Start listener : listeners) {
            listener.onServerLevelTickStart(level);
        }
    });
    public static final EventBus<End> END = EventBus.create(End.class, (listeners) -> (level) -> {
        for (End listener : listeners) {
            listener.onServerLevelTickEnd(level);
        }
    });

    private ServerLevelTickEvents() {}

    public interface Start {
        void onServerLevelTickStart(ServerLevel level);
    }

    public interface End {
        void onServerLevelTickEnd(ServerLevel level);
    }
}
