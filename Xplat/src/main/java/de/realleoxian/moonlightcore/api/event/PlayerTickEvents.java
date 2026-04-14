package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.entity.player.Player;

public final class PlayerTickEvents {
    /**
     * @see Start#onStartPlayerTick(Player)
     */
    public static final EventBus<Start> TICK_START = EventBus.create(Start.class, (listeners) -> (player) -> {
       for(Start listener : listeners) {
           listener.onStartPlayerTick(player);
       }
    });
    /**
     * @see End#onEndPlayerTick(Player)
     */
    public static final EventBus<End> TICK_END = EventBus.create(End.class, (listeners) -> (player) -> {
        for(End listener : listeners) {
            listener.onEndPlayerTick(player);
        }
    });

    private PlayerTickEvents() {}

    public interface Start {
        /**
         * Invoked before a player's tick is processed
         * @param player The player that its ticking
         */
        void onStartPlayerTick(Player player);
    }

    public interface End {
        /**
         * Invoked after a player's tick its processed
         * @param player The player that its ticking
         */
        void onEndPlayerTick(Player player);
    }
}
