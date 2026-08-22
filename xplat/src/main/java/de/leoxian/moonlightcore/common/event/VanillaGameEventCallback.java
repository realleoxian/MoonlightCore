package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import de.leoxian.moonlightcore.common.event.base.EventResult;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface VanillaGameEventCallback {
    Event<VanillaGameEventCallback> EVENT = Event.create(VanillaGameEventCallback.class, callbacks -> (level, event, context, position) -> {
       var result = EventResult.TRUE;
       for (final var callback : callbacks) {
           result = callback.onVanillaGameEvent(level, event, context, position);
           if (result.cancelFurtherEventProcessing()) {
               break;
           }
       }
       return result;
    });

    EventResult onVanillaGameEvent(ServerLevel level, Holder<GameEvent> event, GameEvent.Context context, Vec3 position);
}
