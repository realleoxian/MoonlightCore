package de.leoxian.moonlightcore.event.client;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.client.Minecraft;

public interface ClientLifecycleEvent {
     /**
      * Invoked once the client setup has begun
      */
     Event<Runnable> SETUP = EventFactory.create(Runnable.class);
     /**
      * @see ClientStarted#onLifecycleState(Minecraft)
      */
     Event<ClientStarted> STARTED = EventFactory.create(ClientStarted.class);
     /**
      * @see ClientStopping#onLifecycleState(Minecraft)
      */
     Event<ClientStopping> STOPPING = EventFactory.create(ClientStopping.class);

     /**
      * Invoked of each state of the client's lifecycle
      * @param minecraft The client it's state changed
      */
     void onLifecycleState(Minecraft minecraft);

     interface ClientStarted extends ClientLifecycleEvent {}

     interface ClientStopping extends ClientLifecycleEvent {}
}
