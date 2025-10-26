package de.leoxian.moonlightcore.event.common;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.server.level.ServerLevel;

public interface ServerLevelLifecycleEvent {
     /**
      * @see Load#onLifecycleState(ServerLevel)
      */
     Event<Load> LOAD = EventFactory.create(Load.class);
     /**
      * @see Unload#onLifecycleState(ServerLevel)
      */
     Event<Unload> UNLOAD = EventFactory.create(Unload.class);
     /**
      * @see Save#onLifecycleState(ServerLevel)
      */
     Event<Save> SAVE = EventFactory.create(Save.class);

     /**
      * Invoked on each state of the level's lifecycle
      * @param level The level it's state changed
      */
     void onLifecycleState(ServerLevel level);

     interface Load extends ServerLevelLifecycleEvent {}

     interface Unload extends ServerLevelLifecycleEvent {}

     interface Save extends ServerLevelLifecycleEvent {}
}
