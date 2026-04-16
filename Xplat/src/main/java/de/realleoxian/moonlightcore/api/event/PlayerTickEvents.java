package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.entity.player.Player;

public final class PlayerTickEvents {
    public static final EventBus<Start> TICK_START = EventBus.create(Start.class, (listeners) -> (player) -> {
       for(Start listener : listeners) {
           listener.onStartPlayerTick(player);
       }
    });
    public static final EventBus<End> TICK_END = EventBus.create(End.class, (listeners) -> (player) -> {
        for(End listener : listeners) {
            listener.onEndPlayerTick(player);
        }
    });

    private PlayerTickEvents() {}

    public interface Start {
        void onStartPlayerTick(Player player);
    }

    public interface End {
        void onEndPlayerTick(Player player);
    }
}
