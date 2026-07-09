package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;

public final class ServerPlayerEvents {
    public static final Event<Clone> CLONE = Event.create(Clone.class, listeners -> (original, clone, wasDeath) -> {
       for (final var listener : listeners) {
           listener.onPlayerClone(original, clone, wasDeath);
       }
    });
    public static final Event<AfterRespawn> AFTER_RESPAWN = Event.create(AfterRespawn.class, listeners -> (oldPlayer, newPlayer) -> {
       for (final var listener : listeners) {
           listener.onPlayerRespawn(oldPlayer, newPlayer);
       }
    });
    public static final Event<OpenMenu> OPEN_MENU = Event.create(OpenMenu.class, listeners -> (player, containerMenu) -> {
       for (final var listener : listeners) {
           listener.onOpenMenu(player, containerMenu);
       }
    });
    public static final Event<CloseMenu> CLOSE_MENU = Event.create(CloseMenu.class, listeners -> (player, containerMenu) -> {
        for (final var listener : listeners) {
            listener.onCloseMenu(player, containerMenu);
        }
    });
    public static final Event<ChangeDimension> CHANGE_DIMENSION = Event.create(ChangeDimension.class, listeners -> (player, from, to) -> {
       for (final var listener : listeners) {
           listener.onChangeDimension(player, from, to);
       }
    });

    private ServerPlayerEvents() {}

    @FunctionalInterface
    public interface Clone {
        void onPlayerClone(ServerPlayer original, ServerPlayer clone, boolean wasDeath);
    }

    @FunctionalInterface
    public interface AfterRespawn {
        void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer);
    }

    @FunctionalInterface
    public interface OpenMenu {
        void onOpenMenu(ServerPlayer player, AbstractContainerMenu containerMenu);
    }

    @FunctionalInterface
    public interface CloseMenu {
        void onCloseMenu(ServerPlayer player, AbstractContainerMenu containerMenu);
    }

    @FunctionalInterface
    public interface ChangeDimension {
        void onChangeDimension(ServerPlayer player, ResourceKey<Level> from,  ResourceKey<Level> to);
    }
}
