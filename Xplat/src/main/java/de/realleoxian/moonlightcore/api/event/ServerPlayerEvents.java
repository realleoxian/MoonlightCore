package de.realleoxian.moonlightcore.api.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;

public final class ServerPlayerEvents {
    public static final EventBus<Clone> CLONE = EventBus.create(Clone.class, (listeners) -> (oldPlayer, newPlayer, wasDeath) -> {
       for(Clone listener : listeners) {
           listener.onPlayerClone(oldPlayer, newPlayer, wasDeath);
       }
    });
    public static final EventBus<AfterRespawn> AFTER_RESPAWN = EventBus.create(AfterRespawn.class, (listeners) -> (oldPlayer, newPlayer) -> {
       for(AfterRespawn listener : listeners) {
           listener.onPlayerRespawn(oldPlayer, newPlayer);
       }
    });
    public static final EventBus<OpenMenu> OPEN_MENU = EventBus.create(OpenMenu.class, (listeners) -> (player, menu) -> {
        for(OpenMenu listener : listeners) {
            listener.onPlayerOpenMenu(player, menu);
        }
    });
    public static final EventBus<CloseMenu> CLOSE_MENU = EventBus.create(CloseMenu.class, (listeners) -> (player, menu) -> {
       for(CloseMenu listener : listeners) {
           listener.onPlayerCloseMenu(player, menu);
       }
    });
    public static final EventBus<ChangeDimension> CHANGE_DIMENSION = EventBus.create(ChangeDimension.class, (listeners) -> (player, from, to) -> {
        for(ChangeDimension listener : listeners) {
            listener.onPlayerChangeDimension(player, from, to);
        }
    });

    private ServerPlayerEvents() {}

    public interface Clone {
        void onPlayerClone(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wasDeath);
    }

    public interface AfterRespawn {
        void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer);
    }

    public interface OpenMenu {
        void onPlayerOpenMenu(ServerPlayer player, AbstractContainerMenu menu);
    }

    public interface CloseMenu {
        void onPlayerCloseMenu(ServerPlayer player, AbstractContainerMenu menu);
    }

    public interface ChangeDimension {
        void onPlayerChangeDimension(ServerPlayer player, ResourceKey<Level> from, ResourceKey<Level> to);
    }
}
