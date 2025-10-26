package de.leoxian.moonlightcore.event.common;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;

public interface CommonLifecycleEvent {
     /**
      * Invoked once common setup has begun
      */
     Event<Runnable> SETUP = EventFactory.create(Runnable.class);
}
