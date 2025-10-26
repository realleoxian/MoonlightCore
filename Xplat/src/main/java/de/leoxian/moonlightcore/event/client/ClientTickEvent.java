package de.leoxian.moonlightcore.event.client;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import de.leoxian.moonlightcore.event.common.TickEvent;

public interface ClientTickEvent {
     /**
      * @see ClientTick#onClientTick(TickEvent.Phase)
      */
     Event<ClientTick> CLIENT_TICK = EventFactory.create(ClientTick.class);

     interface ClientTick {
          /**
           * Invoked before and after a client's tick is processed
           * @param phase The phase of the tick, may be {@link TickEvent.Phase#START} or {@link TickEvent.Phase#END}
           */
          void onClientTick(TickEvent.Phase phase);
     }
}
