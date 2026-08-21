package de.leoxian.moonlightcore.internal.common.network.task;

import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import de.leoxian.moonlightcore.internal.common.config.ConfigRegistry;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CSyncLoadedConfigPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

import java.util.Set;
import java.util.function.Consumer;

public record SyncConfigurationTask(ServerConfigurationPacketListenerImpl packetListener, Set<Identifier> syncables) implements ConfigurationTask {
    public static final Type TYPE = new Type("moonlightcoer:sync_config");

    @Override
    public void start(Consumer<Packet<?>> consumer) {
        for (final var syncable : syncables){
            var config = ConfigRegistry.getConfig(syncable);
            if (config == null) {
                continue;
            }
            consumer.accept(new ClientboundCustomPayloadPacket(new S2CSyncLoadedConfigPacket(syncable, config.loadedConfig())));
        }
        ServerConfigurationNetworking.completeTask(packetListener, TYPE);
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
