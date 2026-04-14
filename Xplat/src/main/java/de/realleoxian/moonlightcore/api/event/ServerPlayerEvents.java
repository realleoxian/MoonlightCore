package de.realleoxian.moonlightcore.api.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;

public final class ServerPlayerEvents {
    /**
     * @see Clone#onPlayerClone(ServerPlayer, ServerPlayer, boolean)
     */
    public static final EventBus<Clone> CLONE = EventBus.create(Clone.class, (listeners) -> (oldPlayer, newPlayer, wasDeath) -> {
       for(Clone listener : listeners) {
           listener.onPlayerClone(oldPlayer, newPlayer, wasDeath);
       }
    });
    /**
     * @see AfterRespawn#onPlayerRespawn(ServerPlayer, ServerPlayer)
     */
    public static final EventBus<AfterRespawn> AFTER_RESPAWN = EventBus.create(AfterRespawn.class, (listeners) -> (oldPlayer, newPlayer) -> {
       for(AfterRespawn listener : listeners) {
           listener.onPlayerRespawn(oldPlayer, newPlayer);
       }
    });
    /**
     * @see OpenMenu#onPlayerOpenMenu(ServerPlayer, AbstractContainerMenu)
     */
    public static final EventBus<OpenMenu> OPEN_MENU = EventBus.create(OpenMenu.class, (listeners) -> (player, menu) -> {
        for(OpenMenu listener : listeners) {
            listener.onPlayerOpenMenu(player, menu);
        }
    });
    /**
     * @see CloseMenu#onPlayerCloseMenu(ServerPlayer, AbstractContainerMenu)
     */
    public static final EventBus<CloseMenu> CLOSE_MENU = EventBus.create(CloseMenu.class, (listeners) -> (player, menu) -> {
       for(CloseMenu listener : listeners) {
           listener.onPlayerCloseMenu(player, menu);
       }
    });
    /**
     * @see ChangeDimension#onPlayerChangeDimension(ServerPlayer, ResourceKey, ResourceKey)
     */
    public static final EventBus<ChangeDimension> CHANGE_DIMENSION = EventBus.create(ChangeDimension.class, (listeners) -> (player, from, to) -> {
        for(ChangeDimension listener : listeners) {
            listener.onPlayerChangeDimension(player, from, to);
        }
    });

    private ServerPlayerEvents() {}

    public interface Clone {
        /**
         * Invoked when a player its cloned, this can occur because the player changed dimension,
         * entered the end portal (after the battle) or because the player died. This event may
         * be used to manipulate the new player
         * @param oldPlayer     The player from where we are copying the da
         * @param newPlayer     The new player
         * @param wasDeath      If the clone reason was because of death
         */
        void onPlayerClone(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wasDeath);
    }

    public interface AfterRespawn {
        /**
         * Invoked after a player is respawned
         * @param oldPlayer     The old player
         * @param newPlayer     The new player
         */
        void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer);
    }

    public interface OpenMenu {
        /**
         * Invoked when a player opens a menu
         * @param player    The player that opened the menu
         * @param menu      The menu that was opened
         */
        void onPlayerOpenMenu(ServerPlayer player, AbstractContainerMenu menu);
    }

    public interface CloseMenu {
        /**
         * Invoked when a player closes a menu
         * @param player    The player that closed the menu
         * @param menu      The menu that was closed
         */
        void onPlayerCloseMenu(ServerPlayer player, AbstractContainerMenu menu);
    }

    public interface ChangeDimension {
        /**
         * Invoked when a player changes their dimension
         * @param player    The player that changed dimension
         * @param from      From what dimension the player came from
         * @param to        The dimension the player came to
         */
        void onPlayerChangeDimension(ServerPlayer player, ResourceKey<Level> from, ResourceKey<Level> to);
    }
}
