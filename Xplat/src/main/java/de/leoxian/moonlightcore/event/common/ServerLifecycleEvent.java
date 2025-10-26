package de.leoxian.moonlightcore.event.common;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.server.MinecraftServer;

public interface ServerLifecycleEvent {
     /**
      * @see AboutToStart#onLifecycleState(MinecraftServer)
      */
     Event<AboutToStart> ABOUT_TO_START = EventFactory.create(AboutToStart.class);
     /**
      * @see Starting#onLifecycleState(MinecraftServer)
      */
     Event<Starting> STARTING = EventFactory.create(Starting.class);
     /**
      * @see Started#onLifecycleState(MinecraftServer)
      */
     Event<Started> STARTED = EventFactory.create(Started.class);
     /**
      * @see Stopping#onLifecycleState(MinecraftServer)
      */
     Event<Stopping> STOPPING = EventFactory.create(Stopping.class);
     /**
      * @see Stopped#onLifecycleState(MinecraftServer)
      */
     Event<Stopped> STOPPED = EventFactory.create(Stopped.class);

     /**
      * Invoked on each state of the server's lifecycle
      * @param server The server it's state changed
      */
     void onLifecycleState(MinecraftServer server);

     interface AboutToStart extends ServerLifecycleEvent {}

     interface Starting extends ServerLifecycleEvent {}

     interface Started extends ServerLifecycleEvent {}

     interface Stopping extends ServerLifecycleEvent {}

     interface Stopped extends ServerLifecycleEvent {}
}
