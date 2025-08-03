package de.leowgc.moonlightcore.api.event.server;

import de.leowgc.moonlightcore.api.event.Event;
import de.leowgc.moonlightcore.api.util.SidedEnvironment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

@SidedEnvironment(SidedEnvironment.Environment.SERVER)
public interface ServerPlayerEvents {
    /**
     * An event that is fired when a player joins to the server
     */
    Event<JoinServer> JOIN_SERVER = Event.create();
    /**
     * An event that is fired when a player leaves the server
     */
    Event<LeaveServer> LEAVE_SERVER = Event.create();
    /**
     * An event that is fired after a player has been respawned;
     */
    Event<AfterRespawn> AFTER_RESPAWN = Event.create();
    /**
     * An event that is fired when the data from an old player is copied to a new
     */
    Event<CopyFrom> COPY_FROM = Event.create();

    @FunctionalInterface
    interface JoinServer {
        void bootstrap(MinecraftServer server, ServerPlayer player);
    }

    @FunctionalInterface
    interface LeaveServer {
        void bootstrap(MinecraftServer server);
    }

    @FunctionalInterface
    interface AfterRespawn {
        void bootstrap(Player oldPlayer, Player newPlayer, boolean isAlive);
    }

    @FunctionalInterface
    interface CopyFrom {
        void bootstrap(Player oldPlayer, Player newPlayer, boolean isAlive);
    }
}
