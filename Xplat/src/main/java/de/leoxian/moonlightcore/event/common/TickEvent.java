package de.leoxian.moonlightcore.event.common;


import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface TickEvent {
     /**
      * @see ServerTick#onServerTick(Phase, MinecraftServer)
      */
     Event<ServerTick> SERVER_TICK = EventFactory.create(ServerTick.class);
     /**
      * @see LevelTick#onLevelTick(Phase, Level, boolean)
      */
     Event<LevelTick> LEVEL_TICK = EventFactory.create(LevelTick.class);
     /**
      * @see PlayerTick#onPlayerTick(Phase, Player)
      */
     Event<PlayerTick> PLAYER_TICK = EventFactory.create(PlayerTick.class);

     interface ServerTick {
          /**
           * Invoked before and after a server's tick is processed
           * @param phase The phase of the tick, may be {@link Phase#START} or {@link Phase#END}
           * @param server The instance of the server is ticking
           */
          void onServerTick(Phase phase, MinecraftServer server);
     }

     interface LevelTick {
          /**
           * Invoked before and after a level's tick is processed
           * @param phase The phase of the tick, may be {@link Phase#START} or {@link Phase#END}
           * @param level The instance of the level is ticking
           * @param isClientSide If the level is client-side
           */
          void onLevelTick(Phase phase, Level level, boolean isClientSide);
     }

     interface PlayerTick {
          /**
           * Invoked before and after a player's tick is processed
           * @param phase The phase of the tick, may be {@link Phase#START} or {@link Phase#END}
           * @param player The instance of the player is ticking
           */
          void onPlayerTick(Phase phase, Player player);
     }

     enum Phase {
          START,
          END
     }
}
