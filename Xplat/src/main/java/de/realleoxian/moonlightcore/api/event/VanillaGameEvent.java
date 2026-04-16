package de.realleoxian.moonlightcore.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public interface VanillaGameEvent {
    EventBus<VanillaGameEvent> EVENT = EventBus.create(VanillaGameEvent.class, (listeners) -> (level, event, position, context) -> {
       for(VanillaGameEvent listener : listeners) {
           EventResult result = listener.onVanillaGameEvent(level, event, position, context);

           if(result.cancelFurtherProcessing) {
               return result;
           }
       }

        return EventResult.TRUE;
    });

    EventResult onVanillaGameEvent(ServerLevel level, GameEvent event, Vec3 position, GameEvent.Context context);
}
