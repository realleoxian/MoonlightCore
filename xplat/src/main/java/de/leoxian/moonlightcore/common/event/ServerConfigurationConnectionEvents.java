package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

import java.util.function.Consumer;

public final class ServerConfigurationConnectionEvents {
    public static final Event<Configure> BEFORE_CONFIGURE = Event.create(Configure.class, listeners -> (listener, server, tasksSender, taskFinisher) -> {
       for (Configure configure : listeners) {
           configure.onSendConfiguration(listener, server, tasksSender, taskFinisher);
       }
    });
    public static final Event<Configure> CONFIGURE = Event.create(Configure.class, listeners -> (listener, server, tasksSender, taskFinisher) -> {
        for (Configure configure : listeners) {
            configure.onSendConfiguration(listener, server, tasksSender, taskFinisher);
        }
    });

    @FunctionalInterface
    public interface Configure {
        void onSendConfiguration(ServerConfigurationPacketListenerImpl listener, MinecraftServer server, Consumer<ConfigurationTask> tasksSender, Consumer<ConfigurationTask.Type> taskFinisher);
    }

    private ServerConfigurationConnectionEvents() {}
}
