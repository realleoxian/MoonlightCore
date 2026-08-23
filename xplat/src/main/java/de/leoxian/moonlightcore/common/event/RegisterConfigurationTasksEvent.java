package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public interface RegisterConfigurationTasksEvent {
    Event<RegisterConfigurationTasksEvent> EVENT = Event.create(RegisterConfigurationTasksEvent.class, listeners -> (packetListener, context) -> {
       for (final RegisterConfigurationTasksEvent listener : listeners) {
           listener.onConfigure(packetListener, context);
       }
    });

    void onConfigure(ServerConfigurationPacketListenerImpl packetListener, Context context);

    interface Context {
        void addTask(ConfigurationTask task);
    }
}
