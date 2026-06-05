package de.realleoxian.moonlightcore.api.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

public final class ServerPlayerEvents {
    public static final Event<Clone> CLONE = Event.create(Clone.class);
    public static final Event<AfterRespawn> AFTER_RESPAWN = Event.create(AfterRespawn.class);
    public static final Event<MenuEvent> OPEN_MENU = Event.create(MenuEvent.class);
    public static final Event<MenuEvent> CLOSE_MENU = Event.create(MenuEvent.class);
    public static final Event<ChangeDimension> CHANGE_DIMENSION = Event.create(ChangeDimension.class);

    private ServerPlayerEvents() {}

    public static final class Clone extends EventBase {
        public final ServerPlayer original;
        public final ServerPlayer clone;
        public final boolean wasDeath;

        @ApiStatus.Internal
        public Clone(ServerPlayer original, ServerPlayer clone, boolean wasDeath) {
            this.original = original;
            this.clone = clone;
            this.wasDeath = wasDeath;
        }
    }

    public static final class AfterRespawn extends EventBase {
        public final ServerPlayer oldPlayer;
        public final ServerPlayer newPlayer;

        @ApiStatus.Internal
        public AfterRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
            this.oldPlayer = oldPlayer;
            this.newPlayer = newPlayer;
        }
    }

    public static final class MenuEvent extends EventBase{
        public final ServerPlayer player;
        public final AbstractContainerMenu containerMenu;

        @ApiStatus.Internal
        public MenuEvent(ServerPlayer player, AbstractContainerMenu containerMenu) {
            this.player = player;
            this.containerMenu = containerMenu;
        }
    }

    public static final class ChangeDimension extends EventBase {
        public final ServerPlayer player;
        public final ResourceKey<Level> from;
        public final ResourceKey<Level> to;

        @ApiStatus.Internal
        public ChangeDimension(ServerPlayer player, ResourceKey<Level> from, ResourceKey<Level> levelResourceKey) {
            this.player = player;
            this.from = from;
            to = levelResourceKey;
        }
    }
}
