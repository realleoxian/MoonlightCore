package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

public final class TagsUpdatedEvents {
    public static final Event<ServerDataLoad> SERVER_DATA_LOAD = Event.create(ServerDataLoad.class, listeners -> (registryAccess, resourceManager) -> {
       for (final var listener : listeners) {
           listener.onServerDataLoad(registryAccess, resourceManager);
       }
    });
    public static final Event<ClientPacketReceived> CLIENT_PACKET_RECEIVED = Event.create(ClientPacketReceived.class, listeners -> (registryAccess, isIntegratedServerConnection) -> {
        for (final var listener : listeners) {
            listener.onClientPacketReceived(registryAccess, isIntegratedServerConnection);
        }
    });

    private TagsUpdatedEvents() {}

    @FunctionalInterface
    public interface ServerDataLoad {
        void onServerDataLoad(RegistryAccess registryAccess, ReloadableResourceManager resourceManager);
    }

    @FunctionalInterface
    public interface ClientPacketReceived {
        void onClientPacketReceived(RegistryAccess registryAccess, boolean isIntegratedServerConnection);
    }
}
