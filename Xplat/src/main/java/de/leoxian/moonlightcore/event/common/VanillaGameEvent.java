package de.leoxian.moonlightcore.event.common;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public interface VanillaGameEvent {
     /**
      * @see #onGameEvent(Level, GameEvent, Vec3, GameEvent.Context)
      */
     Event<VanillaGameEvent> EVENT = EventFactory.createWithResult(VanillaGameEvent.class);

     /**
      * Invoked on the server-side of a level whenever a vanilla's {@link GameEvent} is dispatched
      * @param level The level the {@link GameEvent} occurred
      * @param vanillaEvent The vanilla event
      * @param pos The position the event took place at
      * @param context The context of the vanilla event
      * @return A {@link Event.Result} determining the outcome of the event, the action may be cancelled by the result
      */
     Event.Result onGameEvent(Level level, GameEvent vanillaEvent, Vec3 pos, GameEvent.Context context);
}
