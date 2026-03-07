package de.leoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public interface VanillaGameEvent {
    /**
     * @see #onVanillaGameEvent(ServerLevel, GameEvent, Vec3, GameEvent.Context)
     */
    EventBus<VanillaGameEvent> EVENT = EventBus.create((listeners) -> (level, event, position, context) -> {
       for(VanillaGameEvent listener : listeners) {
           EventResult result = listener.onVanillaGameEvent(level, event, position, context);

           if(result.cancelFurtherProcessing) {
               return result;
           }
       }

        return EventResult.TRUE;
    });

    /**
     * Invoked just before a {@link GameEvent} its fired
     * @param level The level the game event it's going to be fired in
     * @param event The event that it's going to be fired
     * @param position The position in the world where the event it's going to be fired
     * @param context The context of the event
     * @return If the event was cancelled or not. If cancelled, the vanilla game event will not be fired
     */
    EventResult onVanillaGameEvent(ServerLevel level, GameEvent event, Vec3 position, GameEvent.Context context);
}
